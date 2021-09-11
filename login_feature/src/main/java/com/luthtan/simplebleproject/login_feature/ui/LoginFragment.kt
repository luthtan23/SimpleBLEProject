package com.luthtan.simplebleproject.login_feature.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.luthtan.simplebleproject.data.repository.PreferencesRepository
import com.luthtan.simplebleproject.data.utils.USER_NAME_KEY_LOGIN_TO_DASHBOARD
import com.luthtan.simplebleproject.data.utils.UUID_KEY_LOGIN_TO_DASHBOARD
import com.luthtan.simplebleproject.login_feature.databinding.FragmentLoginBinding
import com.luthtan.simplebleproject.login_feature.util.DASHBOARD_PACKAGE
import com.luthtan.simplebleproject.login_feature.util.LOGIN_DASHBOARD_NAME_ACTIVITY
import com.luthtan.simplebleproject.common.CustomAlertDialog
import com.luthtan.simplebleproject.common.makeLinks
import com.luthtan.simplebleproject.common.uuidFormatChecker
import com.luthtan.simplebleproject.login_feature.R
import com.luthtan.simplebleproject.login_feature.util.LOGIN_TO_REGISTER_LINK
import org.koin.android.ext.android.inject

class LoginFragment : Fragment() {

    private val preferences: PreferencesRepository by inject()

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

        binding.tvLoginRegister.makeLinks(pairRegisterLink)
    }

    private val pairRegisterLink = Pair(
        LOGIN_TO_REGISTER_LINK, View.OnClickListener {
            registerFragment()
        }
    )

    private fun registerFragment() {
        val registerFragment = RegisterFragment()
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_container, registerFragment, registerFragment.tag)
            addToBackStack(null)
            commit()
        }
    }

    private fun dashboardActivity() {
        val username = binding.etLoginUsername.text
        if (username.isNullOrEmpty()) {
            binding.etLoginUsername.error = "Please fill your username"
            return
        }
        val uuid = binding.etLoginUuid.text
        if (uuid.isNullOrEmpty()) {
            binding.etLoginUuid.error = "Please fill your uuid"
            return
        }
        if (!uuidFormatChecker(uuid.toString())) {
            CustomAlertDialog(
                requireContext(),
                null,
                getString(R.string.login_error_uuid_description),
                false
            ) { dialog, which -> dialog.dismiss() }.show()
            return
        }
        preferences.setUsernameRequest(username.toString())
        preferences.setUuidRequest(uuid.toString())
        actionToDashboardActivity(username.toString(), uuid.toString())
    }

    private fun actionToDashboardActivity(username: String, uuid: String) {
        Intent()
            .putExtra(USER_NAME_KEY_LOGIN_TO_DASHBOARD, username)
            .putExtra(UUID_KEY_LOGIN_TO_DASHBOARD, uuid)
            .setClassName(
            requireContext(),
            DASHBOARD_PACKAGE.plus(LOGIN_DASHBOARD_NAME_ACTIVITY)
        ).also { startActivity(it) }
    }
}