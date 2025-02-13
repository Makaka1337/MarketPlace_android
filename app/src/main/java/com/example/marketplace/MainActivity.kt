package com.example.marketplace

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.marketplace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val PREFS_NAME = "UserPrefs"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_PHONE = "user_phone"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Устанавливаем данные в header
        updateNavHeader()
    }

    private fun updateNavHeader() { // заменяет текст в nav_header_main
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val userName = sharedPreferences.getString(KEY_USER_NAME, "Гость")
        val userPhone = sharedPreferences.getString(KEY_USER_PHONE, "Не указан")

        val headerView = binding.navView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.userNameTextView)
        val userPhoneTextView = headerView.findViewById<TextView>(R.id.textView)

        userNameTextView.text = userName
        if (userPhone != null) { // добавляет +7 если он не указан
            userPhoneTextView.text = if (userPhone.startsWith("+7")) userPhone else "+7$userPhone"
        }

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean { //создаёт менюшку (троеточие) с кнопкой выхода из аккаунта
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // выход из акканта
        return when (item.itemId) {
            R.id.action_settings -> {
                logoutUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logoutUser() { // чистит сессию
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
