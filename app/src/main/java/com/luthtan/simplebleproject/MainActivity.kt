package com.luthtan.simplebleproject

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.luthtan.simplebleproject.data.repository.PreferencesRepository
import com.luthtan.simplebleproject.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val preferences: PreferencesRepository by inject()

    companion object {
        const val DASHBOARD_PACKAGE = "com.luthtan.simplebleproject.dashboard_feature"
        const val LOGIN_PACKAGE = "com.luthtan.simplebleproject.login_feature"

        const val NAME_LOGIN_ACTIVITY = "LoginActivity"
        const val NAME_DASHBOARD_ACTIVITY = "DashboardActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val activityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        when(preferences.getAdvertisingState()) {
            true -> Intent().setClassName(this, "$DASHBOARD_PACKAGE.$NAME_DASHBOARD_ACTIVITY").also { startActivity(it) }
            false -> Intent().setClassName(this, "$LOGIN_PACKAGE.$NAME_LOGIN_ACTIVITY").also { startActivity(it) }
        }

        finish()



    }
}