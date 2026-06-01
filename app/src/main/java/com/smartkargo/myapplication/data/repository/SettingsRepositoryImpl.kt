package com.smartkargo.myapplication.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.smartkargo.myapplication.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val CURRENCY_KEY = stringPreferencesKey("currency")
        val PIN_KEY = stringPreferencesKey("pin")
        val BIOMETRIC_KEY = booleanPreferencesKey("biometric_enabled")
    }

    override fun isDarkMode(): Flow<Boolean> =
        dataStore.data.map { it[DARK_MODE_KEY] ?: false }

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[DARK_MODE_KEY] = enabled }
    }

    override fun getCurrency(): Flow<String> =
        dataStore.data.map { it[CURRENCY_KEY] ?: "USD" }

    override suspend fun setCurrency(currency: String) {
        dataStore.edit { it[CURRENCY_KEY] = currency }
    }

    override fun getPin(): Flow<String?> =
        dataStore.data.map { it[PIN_KEY] }

    override suspend fun setPin(pin: String) {
        dataStore.edit { it[PIN_KEY] = pin }
    }

    override fun isBiometricEnabled(): Flow<Boolean> =
        dataStore.data.map { it[BIOMETRIC_KEY] ?: false }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { it[BIOMETRIC_KEY] = enabled }
    }
}

