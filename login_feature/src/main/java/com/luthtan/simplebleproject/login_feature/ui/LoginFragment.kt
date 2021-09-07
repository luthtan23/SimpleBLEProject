package com.luthtan.simplebleproject.login_feature.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.luthtan.simplebleproject.util.DASHBOARD_PACKAGE
import com.luthtan.simplebleproject.data.repository.PreferencesRepository
import com.luthtan.simplebleproject.login_feature.R
import com.luthtan.simplebleproject.login_feature.databinding.FragmentLoginBinding
import com.luthtan.simplebleproject.login_feature.util.LOGIN_DASHBOARD_NAME_ACTIVITY
import com.luthtan.simplebleproject.login_feature.util.LOGIN_TO_REGISTER_LINK
import com.luthtan.simplebleproject.login_feature.util.makeLinks
import org.koin.android.ext.android.inject

class LoginFragment : Fragment() {

    private val preference: PreferencesRepository by inject()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLoginLogin.setOnClickListener { dashboardActivity() }

//        binding.tvLoginRegister.makeLinks(pairRegisterLink)
    }

   /* private val pairRegisterLink = Pair(
        LOGIN_TO_REGISTER_LINK, View.OnClickListener {
            registerFragment()
        }
    )
*/
    /*private fun registerFragment() {
        val registerFragment = RegisterFragment()
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_login_container, registerFragment, registerFragment.tag)
            addToBackStack(null)
            commit()
        }
    }*/

    private fun dashboardActivity() {
        Intent().setClassName(
            requireContext(),
            DASHBOARD_PACKAGE.plus(LOGIN_DASHBOARD_NAME_ACTIVITY)
        ).also { startActivity(it) }
    }
}