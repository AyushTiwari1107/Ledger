package com.smartkargo.myapplication.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun isDarkMode(): Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)
    fun getCurrency(): Flow<String>
    suspend fun setCurrency(currency: String)
    fun getPin(): Flow<String?>
    suspend fun setPin(pin: String)
    fun isBiometricEnabled(): Flow<Boolean>
    suspend fun setBiometricEnabled(enabled: Boolean)
}

