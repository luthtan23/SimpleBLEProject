package com.luthtan.simplebleproject.data.repository

import com.luthtan.simplebleproject.data.local.preferences.PreferenceConstants
import com.luthtan.simplebleproject.data.local.preferences.PreferenceHelper


class PreferencesRepository(private val preferences: PreferenceHelper) {

    fun setIsDarkMode(status: Boolean) {
        preferences.put(PreferenceConstants.IS_DARK_MODE, status)
    }

    fun getIsDarkMode(): Boolean {
        return preferences.getBoolean(PreferenceConstants.IS_DARK_MODE)
    }

    fun setAdvertisingState(status: Boolean) {
        preferences.put(PreferenceConstants.STATE_ADVERTISING, status)
    }

    fun getAdvertisingState(): Boolean {
        return preferences.getBoolean(PreferenceConstants.STATE_ADVERTISING)
    }

    fun getUsernameRequest(): String {
        return preferences.get(PreferenceConstants.USERNAME_REQUEST)
    }

    fun getPasswordRequest(): String {
        return preferences.get(PreferenceConstants.PASSWORD_REQUEST)
    }

}