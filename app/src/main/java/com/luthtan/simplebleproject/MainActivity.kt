package com.luthtan.simplebleproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.luthtan.simplebleproject.data.repository.PreferencesRepository
import com.luthtan.simplebleproject.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val preferences: PreferencesRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        Intent().setClassName(this, "$LOGIN_PACKAGE.$NAME_LOGIN_ACTIVITY").also { startActivity(it) }
        finish()

        val activityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

    }
}