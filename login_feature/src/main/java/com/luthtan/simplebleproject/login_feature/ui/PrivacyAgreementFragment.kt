package com.luthtan.simplebleproject.login_feature.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import com.luthtan.simplebleproject.data.repository.dashboard.DashboardRepository
import com.luthtan.simplebleproject.login_feature.LoginActivity
import com.luthtan.simplebleproject.login_feature.R
import com.luthtan.simplebleproject.login_feature.databinding.FragmentPrivacyAgreementBinding
import com.luthtan.simplebleproject.login_feature.util.PRIVACY_AGREEMENT_TO_REGISTER_REQUEST_KEY
import com.luthtan.simplebleproject.login_feature.util.PRIVACY_AGREEMENT_TO_REGISTER_STATE_KEY
import org.koin.android.ext.android.inject

class PrivacyAgreementFragment : Fragment() {

    private val dashboardRepository: DashboardRepository by inject()

    private var _binding: FragmentPrivacyAgreementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivacyAgreementBinding.inflate(inflater, container, false)
        val actionBar = (requireActivity() as LoginActivity)
        actionBar.setSupportActionBar(binding.toolbarPrivacyAgreement)
        actionBar.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dashboardRepository.getUserData().observe(viewLifecycleOwner, { apiResponse ->
            val response = apiResponse.body
            if (response != null) {
                binding.tvPrivacyAgreementText.text = response.nameUser
            }
        })

        binding.btnPrivacyAgreementAccept.setOnClickListener{ backToRegisterWithAcceptable() }
    }

    private fun backToRegisterWithAcceptable() {
        val state = true
        setFragmentResult(PRIVACY_AGREEMENT_TO_REGISTER_REQUEST_KEY, bundleOf(
            PRIVACY_AGREEMENT_TO_REGISTER_STATE_KEY to state))
        backToRegister()
    }

    private fun backPressedFragment() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun backToRegister() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> backToRegister()
        }
        return super.onOptionsItemSelected(item)
    }


}