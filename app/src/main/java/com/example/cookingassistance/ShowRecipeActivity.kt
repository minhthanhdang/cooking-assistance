package com.example.cookingassistance

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class ShowRecipeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    lateinit var recyclerView: RecyclerView
    lateinit var toolbar: Toolbar
    var mainImgBmp: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_recipe)


        // GET RECIPE ID
        val recipeID = intent.getIntExtra("recipeID", 0)


        // DATABASE SETUP
        val db = Room.databaseBuilder(
            applicationContext,
            RecipeDatabase::class.java, "recipes"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()

        val recipeDAO = db.recipeDAO()
        val ingredientDAO = db.ingredientDAO()
        val stepDAO = db.stepDAO()


        // MAIN NAVIGATION COMPONENTS
        drawerLayout = findViewById(R.id.drawer_layout)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navView = findViewById(R.id.nav_view)
        navView.bringToFront()
        navView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()



        // RECYCLER VIEW
        recyclerView = findViewById(R.id.food_list)

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager


        // FETCH DATA
        var ingredientData = mutableListOf<Ingredient>()
        var stepData = mutableListOf<Step>()
        ingredientData.addAll(ingredientDAO.getIngredientFromRecipe(recipeID))
        stepData.addAll(stepDAO.getStepFromRecipeID(recipeID))

        val ingredientAdapter = ShowIngredientsAdapter(ingredientData)
        val stepAdapter = ShowStepsAdapter(stepData)
        recyclerView.adapter = ingredientAdapter

        findViewById<EditText>(R.id.main_name).setText(recipeDAO.getRecipeNameFromID(recipeID))


        // INGREDIENT/STEP CHIPS
        val chips = findViewById<ChipGroup>(R.id.step_or_ingre)
        val ingreChip = findViewById<Chip>(R.id.add_ingredient_chip)
        ingreChip.isChecked = true


        // DEFAULT HIDE ADD BUTTON
        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add)
        fabAdd.isClickable = false
        fabAdd.isFocusable = false
        fabAdd.alpha = 0f
        // ADD ITEM
        fabAdd.setOnClickListener {
            when (chips.checkedChipId) {
                R.id.add_step_chip -> {
                    stepData = stepAdapter.getStepData()
                    stepData.add(Step("", stepData.size + 2, recipeID))
                    stepAdapter.notifyDataSetChanged()
                    val count = stepData.size - 1
                    recyclerView.scrollToPosition(count)
                }
                R.id.add_ingredient_chip -> {
                    ingredientData = ingredientAdapter.getIngredientData()
                    ingredientData.add(Ingredient("", "", recipeID))
                    ingredientAdapter.notifyDataSetChanged()
                    val count = ingredientAdapter.itemCount - 1
                    recyclerView.scrollToPosition(count)
                }
            }
        }


        var editing = false
        val changeImage = findViewById<TextView>(R.id.change_image)
        val deleteButton = findViewById<Button>(R.id.delete_recipe)

        // SAVE EDIT BUTTON
        val fabSave = findViewById<FloatingActionButton>(R.id.fab_save)
        // SAVE ITEM
        fabSave.setOnClickListener {
            if (!editing){
                fabAdd.isClickable = true
                fabAdd.isFocusable = true
                fabAdd.alpha = 1f
                fabSave.setImageResource(R.drawable.save)
                editing = true
                changeImage.isVisible = true
                deleteButton.isVisible = true

                ingredientAdapter.startEditing()
                stepAdapter.startEditing()
            } else {
                fabSave.setImageResource(R.drawable.edit)
                editing = false
                fabAdd.isClickable = false
                fabAdd.isFocusable = false
                fabAdd.alpha = 0f
                ingredientAdapter.stopEditing()
                stepAdapter.stopEditing()
                changeImage.isVisible = false
                deleteButton.isVisible = false

                // get data back from adapter
                ingredientData = ingredientAdapter.getIngredientData()
                stepData = stepAdapter.getStepData()
                var deletedIngredientData = ingredientAdapter.getDeletedIngredients()
                var deletedStep = stepAdapter.getDeletedStep()

                // UPDATE RECIPE IN RECIPE TABLE
                val recipeName = findViewById<EditText>(R.id.main_name).text.toString()
                recipeDAO.upsertRecipe(Recipe(recipeName, recipeID))


                // UPDATE PHOTO
                mainImgBmp?.let {mainBmp ->
                    try {
                        savePhotoToInternalStorage("Recipe$recipeID", mainBmp)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } ?: run {
                    val dr = ContextCompat.getDrawable(this, R.drawable.blank_recipe)
                    val bitmap = dr?.toBitmap()
                    bitmap?.let {bitmap ->
                        savePhotoToInternalStorage("Recipe$recipeID", bitmap)
                    }
                }


                // DELETE OLD DATA
                for (i in deletedIngredientData) {
                    var toDelete = ingredientDAO.getIngredientIfIDExist(i.ingredientID)
                    if (toDelete != null) {
                        ingredientDAO.deleteIngredient(toDelete)
                    }
                }
                for (i in deletedStep) {
                    var toDelete = stepDAO.getStepIfIDExist(i.stepID)
                    if (toDelete != null) {
                        stepDAO.deleteStep(toDelete)
                    }
                }


                // INSERT OR UPGRADE ingredients and steps IN THE DATABASE
                for (i in 0 until ingredientData.size) {
                    ingredientDAO.upsertIngredient(ingredientData[i])
                }
                for (i in 0 until stepData.size) {
                    stepDAO.upsertStep(stepData[i])
                }
            }
        }


        // INGREDIENT/STEP CHIPS
        chips.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.add_step_chip -> recyclerView.adapter = stepAdapter
                R.id.add_ingredient_chip -> recyclerView.adapter = ingredientAdapter
            }
        }


        // MAIN IMAGE
        val mainImg = findViewById<ImageView>(R.id.main_img)
        var mainImgBmp = getPhotoFromInternalStorage("Recipe$recipeID.jpg")
        mainImg.setImageBitmap(mainImgBmp)
        mainImg.setOnClickListener {
            if (editing) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startForImageResult.launch(intent)
            }
        }

        // DELETE BUTTON
        deleteButton.setOnClickListener {
            for (item in ingredientDAO.getIngredientFromRecipe(recipeID)) {
                ingredientDAO.deleteIngredient(item)
            }
            for (item in stepDAO.getStepFromRecipeID(recipeID)) {
                stepDAO.deleteStep(item)
            }
            recipeDAO.deleteRecipe(recipeDAO.getRecipeFromID(recipeID))

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId) {
            R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.add_recipe -> {
                val intent = Intent(this, AddRecipeActivity::class.java)
                startActivity(intent)
            }
            R.id.clock -> {
                val intent = Intent(this, Clock::class.java)
                startActivity(intent)
            }
            R.id.about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }

        }

        return true
    }

    val startForImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            when(result.resultCode) {
                RESULT_OK -> {
                    val data = result.data
                    if (data != null) {
                        val selectedImage = data.getData()
                        val imgView = findViewById<ImageView>(R.id.main_img)
                        selectedImage?.let {nonNullUri ->
                            try {
                                imgView.setImageURI(selectedImage)
                                mainImgBmp = getBitmapFromUri(selectedImage)
                                val addImage = findViewById<TextView>(R.id.add_image)
                                addImage.isVisible = false
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        }

                    }
                }
            }
        }

    private fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Boolean {
        return try {
            openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap")
                }
                true
            }
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun getPhotoFromInternalStorage(filename: String): Bitmap? {
        val file = File(filesDir, filename)
        val bytes = file.readBytes()
        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        return bmp
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}