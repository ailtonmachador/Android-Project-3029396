package com.griffith.user5

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /**
     * Configura o DrawerLayout, Toolbar e NavigationView.
     */
    protected fun setupDrawer(toolbarId: Int, drawerLayoutId: Int, navigationViewId: Int) {
        // Configura o DrawerLayout e a Toolbar
        drawerLayout = findViewById(drawerLayoutId)
        navigationView = findViewById(navigationViewId)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(toolbarId)
        setSupportActionBar(toolbar)

        // Configura o toggle do DrawerLayout para abrir e fechar o menu
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Configura o NavigationView para lidar com a seleção dos itens de navegação
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_request_day_off -> navigateTo(RequestActivity::class.java)
                R.id.nav_notifications -> navigateTo(NotificationActivity::class.java)
                R.id.nav_clock_in -> navigateTo(ClockInActivity::class.java)
                R.id.nav_logout -> performLogout()
            }
            drawerLayout.closeDrawers() // Fecha o drawer após a seleção
            true
        }
    }

    /**
     * Realiza o logout e redireciona para a tela de login.
     */
    private fun performLogout() {
        // Limpar as informações de login e redirecionar para LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Finaliza a atividade atual para que o usuário não volte para ela ao pressionar "voltar"
    }

    /**
     * Navega para outra atividade.
     */
    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
