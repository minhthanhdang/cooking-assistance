package com.example.cookingassistance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView

class Clock : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)

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



        // 2 FRAGMENTS
        val timerFragment = TimerFragment()
        val stopwatchFragment = StopwatchFragment()


        // SET TIMER AS INITIAL FRAGMENT
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.clock_fragment, timerFragment)
            commit()
        }



        // SET THE 2 BUTTONS TO CHANGE THE DISPLAY FRAGMENT
        val timer = findViewById<Button>(R.id.timer)
        val stopwatch = findViewById<Button>(R.id.stopwatch)

        timer.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.clock_fragment, timerFragment)
                commit()
            }
        }

        stopwatch.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.clock_fragment, stopwatchFragment)
                commit()
            }
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
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
        }

        return true
    }
}