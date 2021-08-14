package com.luthtan.simplebleproject

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.luthtan.simplebleproject.data.repository.PreferencesRepository
import com.luthtan.simplebleproject.databinding.ActivityMainBinding
import com.luthtan.simplebleproject.login.LoginActivity
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val preferences: PreferencesRepository by inject()

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        identifyThemeMode(this)

        Intent(this, LoginActivity::class.java).also { startActivity(it) }
        finish()

        val activityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

    }

    fun identifyThemeMode(context: Context) {
        when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> preferences.setIsDarkMode(true)
            Configuration.UI_MODE_NIGHT_NO -> preferences.setIsDarkMode(false)
            Configuration.UI_MODE_NIGHT_UNDEFINED -> { }
        }
    }
}