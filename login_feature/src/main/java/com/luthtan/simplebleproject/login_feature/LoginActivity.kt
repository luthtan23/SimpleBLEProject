package com.luthtan.simplebleproject.login_feature

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.luthtan.simplebleproject.login_feature.databinding.ActivityLoginBinding
import com.luthtan.simplebleproject.login_feature.ui.LoginFragment

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().apply {
            add(R.id.frame_container, LoginFragment(), LoginFragment::getTag.toString())
            commit()
        }

    }


}