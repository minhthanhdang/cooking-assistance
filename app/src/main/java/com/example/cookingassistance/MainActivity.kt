package com.example.cookingassistance

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var foodList: RecyclerView
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)


        // SETUP DATABASE
        val db = Room.databaseBuilder(
            applicationContext,
            RecipeDatabase::class.java, "recipes"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()

        val recipeDAO = db.recipeDAO()


        // SETUP RECIPES DATA
        val allData = mutableListOf<Recipe>()

        val recipes = recipeDAO.getAllRecipe()
        for (recipe in recipes) {
            allData.add(recipe)
        }


        // TOOLBAR
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        // NAV VIEW AND DRAWER LAYOUT
        navView = findViewById(R.id.nav_view)
        navView.bringToFront()
        navView.setNavigationItemSelectedListener(this)


        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        // SEARCH FUNCTION
        val searchBox = findViewById<EditText>(R.id.searchbox)
        var viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)


        var data = mutableListOf<Recipe>()
        data.addAll(allData)

        // RECYCLER VIEW
        foodList = findViewById(R.id.food_list)
        val gridLayoutManager = GridLayoutManager(this, 2)
        foodList.layoutManager = gridLayoutManager
        foodList.setHasFixedSize(true)

        val adapter = CardViewAdapter(this, data) {showRecipe(it)}
        foodList.adapter = adapter


        viewModel.search.observe(this, Observer{

            data.clear()
            Log.i("haha", allData.toString())
            data.addAll(allData.filter {item -> item.name.contains(it, ignoreCase = true)})
            adapter.notifyDataSetChanged()
        })

        searchBox.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }


    // ON RECIPE CLICK, NAVIGATE TO SHOW RECIPE ACTIVITY
    private fun showRecipe(item: Recipe) {
        Log.i( "haha", item.name)
        val intent = Intent(this, ShowRecipeActivity::class.java)
        intent.putExtra("recipeID", item.recipeID)
        startActivity(intent)
    }




    // OVERRIDE ONBACKPRESSED TO ADAPT WITH MENU
    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    // ON CLICKING ITEMS ON THE MENU
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId) {
            R.id.home -> {
                drawerLayout.closeDrawer(GravityCompat.START)
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
}
