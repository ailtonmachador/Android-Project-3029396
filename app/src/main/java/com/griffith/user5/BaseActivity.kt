package com.griffith.user5

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
/**
 * BaseActivity class serves as the base class for activities that require a navigation drawer.
 * It sets up a common DrawerLayout, Toolbar, and NavigationView, and manages navigation item selections.
 */
abstract class BaseActivity : AppCompatActivity() {
    // UI components
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    // Flag to determine if the logged-in user is a manager
    private var isManager: Boolean = false
    private lateinit var userRepository: UserRepository
    private val userDatabaseHelper = UserDatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userRepository = UserRepository(this)

    }

    /**
     * set up DrawerLayout, Toolbar e NavigationView.
     */
    protected fun setupDrawer(toolbarId: Int, drawerLayoutId: Int, navigationViewId: Int) {
        // set up  DrawerLayout and Toolbar
        drawerLayout = findViewById(drawerLayoutId)
        navigationView = findViewById(navigationViewId)



        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(toolbarId)
        setSupportActionBar(toolbar)

        // set up toggle
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        // Check if the logged-in user is a manager
        val loggedInUser = userRepository.getLoggedInUserName()
        isManager = loggedInUser?.let {
            userRepository.checkUserAdmin(userDatabaseHelper.readableDatabase, it)
        } ?: false

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //set uo NavigationView to handle nav itens
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_request_day_off -> navigateTo(SelectDayOff::class.java)
                R.id.nav_notifications -> navigateTo(NotificationActivity::class.java)
                R.id.nav_clock_in -> {
                    if (isManager) {
                        navigateTo(Map::class.java)
                    } else {
                        navigateTo(ClockInActivity::class.java)
                    }
                }
                        R.id.nav_logout -> performLogout()

            }
            drawerLayout.closeDrawers() // Close the drawer after a selection is made
            true
        }
    }

    /**
     * do logout and redirect to login page
     */
    private fun performLogout() {
        // Clear login information and navigate to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()  // Finish the current activity to prevent the user from returning to it
    }


    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

}
