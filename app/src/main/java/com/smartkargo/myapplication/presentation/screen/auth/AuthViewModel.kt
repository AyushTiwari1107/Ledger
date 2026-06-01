package com.smartkargo.myapplication.presentation.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkargo.myapplication.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val pin: String = "",
    val savedPin: String? = null,
    val isSettingPin: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val biometricEnabled: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.getPin(),
                settingsRepository.isBiometricEnabled()
            ) { pin, biometric ->
                _uiState.update {
                    it.copy(
                        savedPin = pin,
                        isSettingPin = pin == null,
                        biometricEnabled = biometric
                    )
                }
            }.collect()
        }
    }

    fun onPinEntered(pin: String) {
        val state = _uiState.value
        if (state.isSettingPin) {
            viewModelScope.launch {
                settingsRepository.setPin(pin)
                _uiState.update { it.copy(isAuthenticated = true) }
            }
        } else {
            if (pin == state.savedPin) {
                _uiState.update { it.copy(isAuthenticated = true, error = null) }
            } else {
                _uiState.update { it.copy(error = "Incorrect PIN", pin = "") }
            }
        }
    }

    fun onBiometricSuccess() {
        _uiState.update { it.copy(isAuthenticated = true) }
    }
}
