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

class AddRecipeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    lateinit var recyclerView: RecyclerView
    lateinit var toolbar: Toolbar
    var mainImgBmp: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_recipe)


        // DATABASE SETUP
        val db = Room.databaseBuilder(
            applicationContext,
            RecipeDatabase::class.java, "recipes"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()

        val recipeDao = db.recipeDAO()
        val ingredientDAO = db.ingredientDAO()
        val stepDAO = db.stepDAO()


        // MAIN NAVIGATION COMPONENTS
        drawerLayout = findViewById(R.id.drawer_layout)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navView = findViewById(R.id.nav_view)
        navView.bringToFront()
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)


        // RECYCLER VIEW
        recyclerView = findViewById(R.id.food_list)

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager


        // SETUP EMPTY DATA
        var ingredientData = mutableListOf<String>()
        var amountData = mutableListOf<String>()
        var stepData = mutableListOf<String>()
        ingredientData.add("")
        amountData.add("")
        stepData.add("")



        val ingredientAdapter = AddAdapter(ingredientData, amountData)
        val stepAdapter = StepAdapter(stepData)
        recyclerView.adapter = ingredientAdapter


        // INGREDIENT/STEP CHIPS
        val chips = findViewById<ChipGroup>(R.id.step_or_ingre)
        val ingreChip = findViewById<Chip>(R.id.add_ingredient_chip)
        ingreChip.isChecked = true



        // ADD ITEM
        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            when (chips.checkedChipId) {
                R.id.add_step_chip -> {
                    stepData = stepAdapter.getStep()
                    stepData.add("")
                    stepAdapter.notifyDataSetChanged()
                    val count = stepAdapter.itemCount - 1
                    recyclerView.scrollToPosition(count)
                }
                R.id.add_ingredient_chip -> {
                    ingredientData = ingredientAdapter.getIngredientData()
                    amountData = ingredientAdapter.getAmountData()
                    ingredientData.add("")
                    amountData.add("")
                    ingredientAdapter.notifyDataSetChanged()
                    val count = ingredientAdapter.itemCount - 1
                    recyclerView.scrollToPosition(count)
                }
            }
        }


        // SAVE ITEM
        findViewById<FloatingActionButton>(R.id.fab_save).setOnClickListener {
            // get data back from adapter
            ingredientData = ingredientAdapter.getIngredientData()
            amountData = ingredientAdapter.getAmountData()
            stepData = stepAdapter.getStep()
            val recipeName = findViewById<EditText>(R.id.main_name).text.toString()


            // add recipe to database

            recipeDao.upsertRecipe(Recipe(recipeName))
            val recipeID = recipeDao.getLatestRecipeID()


            // save photo to internal storage
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


            // add ingredients and steps to the database
            for (i in 0 until ingredientData.size) {
                ingredientDAO.upsertIngredient(Ingredient(ingredientData[i], amountData[i], recipeID))
            }
            for (i in 0 until stepData.size) {
                stepDAO.upsertStep(Step(stepData[i], i, recipeID))
            }



            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        chips.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.add_step_chip -> recyclerView.adapter = stepAdapter
                R.id.add_ingredient_chip -> recyclerView.adapter = ingredientAdapter
            }
        }


        // MAIN IMAGE
        val mainImg = findViewById<ImageView>(R.id.main_img)
        mainImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startForImageResult.launch(intent)
        }

    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId) {
            R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.add_recipe -> {
                drawerLayout.closeDrawer(GravityCompat.START)
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