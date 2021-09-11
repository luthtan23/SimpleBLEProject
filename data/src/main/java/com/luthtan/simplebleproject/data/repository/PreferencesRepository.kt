package com.luthtan.simplebleproject.data.repository

import com.luthtan.simplebleproject.data.local.preferences.PreferenceConstants
import com.luthtan.simplebleproject.data.local.preferences.PreferenceHelper


class PreferencesRepository(private val preferences: PreferenceHelper) {

    fun setIsDarkMode(status: Boolean) = preferences.put(PreferenceConstants.IS_DARK_MODE, status)
    fun getIsDarkMode(): Boolean = preferences.getBoolean(PreferenceConstants.IS_DARK_MODE)

    fun setAdvertisingState(status: Boolean) = preferences.put(PreferenceConstants.STATE_ADVERTISING, status)
    fun getAdvertisingState(): Boolean = preferences.getBoolean(PreferenceConstants.STATE_ADVERTISING)

    fun setUsernameRequest(username: String) = preferences.put(PreferenceConstants.USERNAME_REQUEST, username)
    fun getUsernameRequest(): String = preferences.get(PreferenceConstants.USERNAME_REQUEST)

    fun setPasswordRequest(password: String) = preferences.put(PreferenceConstants.PASSWORD_REQUEST, password)
    fun getPasswordRequest(): String = preferences.get(PreferenceConstants.PASSWORD_REQUEST)

    fun setUuidRequest(uuidStr: String) = preferences.put(PreferenceConstants.UUID_REQUEST, uuidStr)
    fun getUuidRequest(): String = preferences.get(PreferenceConstants.UUID_REQUEST)

}