package com.kastapp.sample.ui

import android.os.Bundle
import android.view.Menu
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.kastapp.sample.R
import com.kastapp.sample.databinding.ActivityMainBinding
import com.kastapp.sample.ui.common.AbsActivity

class MainActivity : AbsActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val navController by lazy { findNavController(R.id.navigation_host) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            setSupportActionBar(toolbar)
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_user,
                    R.id.nav_news,
                    R.id.nav_permission
                ), drawer
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navigation.setupWithNavController(navController)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
