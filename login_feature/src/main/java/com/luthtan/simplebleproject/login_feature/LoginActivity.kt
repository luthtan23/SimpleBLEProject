package com.luthtan.simplebleproject.login_feature

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.luthtan.simplebleproject.login_feature.databinding.ActivityLoginBinding

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

        /*supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_login_container, LoginFragment(), LoginFragment::getTag.toString())
            commit()
        }*/

    }


}