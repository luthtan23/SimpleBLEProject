package com.luthtan.simplebleproject.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.luthtan.simplebleproject.R
import com.luthtan.simplebleproject.util.DASHBOARD_PACKAGE
import com.luthtan.simplebleproject.util.LOGIN_PACKAGE

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContentView(R.layout.activity_splash_screen)

        /*Intent(this, MainActivity::class.java).also { startActivity(it) }
        finish()*/

        Intent().setClassName(this, "$LOGIN_PACKAGE.LoginActivity").also { startActivity(it) }
        finish()
    }
}