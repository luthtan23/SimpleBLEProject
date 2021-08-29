package com.luthtan.simplebleproject.login_feature.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.luthtan.simplebleproject.data.repository.PreferencesRepository
import com.luthtan.simplebleproject.login_feature.R
import com.luthtan.simplebleproject.login_feature.databinding.FragmentRegisterBinding
import com.luthtan.simplebleproject.login_feature.util.*
import org.koin.android.ext.android.inject

class RegisterFragment : Fragment(), View.OnClickListener {

    private val preference: PreferencesRepository by inject()

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(PRIVACY_AGREEMENT_TO_REGISTER_REQUEST_KEY) { requestKey, bundle ->
            val result = bundle.getBoolean(PRIVACY_AGREEMENT_TO_REGISTER_STATE_KEY)
            binding.checkboxRegisterPrivacyAgreement.isChecked = result
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.tvLoginRegister.makeLinks(pairMakeLinkRegister)

        binding.tvRegisterPrivacyAgreement.makeLinks(pairMakeLinkPrivacyAgreement)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()


    }

    override fun onStop() {
        super.onStop()

    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    override fun onClick(v: View) {
        when(v.id) {

        }
    }

    private val acceptPrivacyAgreement = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result: ActivityResult ->
    }


    private val pairMakeLinkRegister = Pair(REGISTER_TO_LOGIN_LINK, View.OnClickListener {
        requireActivity().supportFragmentManager.popBackStack()
    })

    private val pairMakeLinkPrivacyAgreement = Pair(REGISTER_TO_PRIVACY_AGREEMENT_LINK, View.OnClickListener {
        privacyAgreementFragment()
    })

    private fun privacyAgreementFragment() {
        val privacyAgreementFragment = PrivacyAgreementFragment()
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_login_container, privacyAgreementFragment, privacyAgreementFragment.tag)
            addToBackStack(null)
            commit()
        }
    }



}