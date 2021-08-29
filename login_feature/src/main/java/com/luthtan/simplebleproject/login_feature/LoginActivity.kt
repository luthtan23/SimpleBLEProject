package com.luthtan.simplebleproject.login_feature

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.luthtan.simplebleproject.DASHBOARD_PACKAGE
import com.luthtan.simplebleproject.login_feature.databinding.ActivityLoginBinding
import com.luthtan.simplebleproject.login_feature.ui.LoginFragment
import com.luthtan.simplebleproject.login_feature.util.LOGIN_DASHBOARD_NAME_ACTIVITY
import com.luthtan.simplebleproject.login_feature.util.LOGIN_TO_REGISTER_LINK
import com.luthtan.simplebleproject.login_feature.util.makeLinks

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*binding.btnLoginLogin.setOnClickListener {
            Intent().setClassName(this, DASHBOARD_PACKAGE.plus(LOGIN_DASHBOARD_NAME_ACTIVITY)).also { startActivity(it) }
        }

        binding.tvLoginRegister.makeLinks(
            Pair(LOGIN_TO_REGISTER_LINK, View.OnClickListener {
                Toast.makeText(this, "JOSS REGISTER", Toast.LENGTH_SHORT).show()
            })
        )*/

        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_login_container, LoginFragment(), LoginFragment::getTag.toString())
            commit()
        }

    }


}