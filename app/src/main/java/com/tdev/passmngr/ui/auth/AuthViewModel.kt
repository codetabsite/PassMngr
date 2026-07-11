package com.tdev.passmngr.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.tdev.passmngr.util.PinManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class AuthState {
    object Idle : AuthState()
    object BiometricPrompt : AuthState()
    object PinEntry : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state = _state.asStateFlow()

    val pinIsSet: Boolean get() = PinManager.isSet(getApplication())

    fun startAuth() {
        _state.value = AuthState.BiometricPrompt
    }

    fun onBiometricSuccess() {
        _state.value = AuthState.Success
    }

    fun onBiometricUnavailable() {
        // Biometric yok ya da kayıtlı değil — PIN'e düş
        if (pinIsSet) {
            _state.value = AuthState.PinEntry
        } else {
            // Hiç güvenlik kurulmamış, PIN kurulumuna yönlendir
            _state.value = AuthState.PinEntry
        }
    }

    fun checkPin(pin: String) {
        if (PinManager.check(getApplication(), pin)) {
            _state.value = AuthState.Success
        } else {
            _state.value = AuthState.Error("Yanlış PIN")
            _state.value = AuthState.PinEntry
        }
    }

    fun setPin(pin: String) {
        PinManager.set(getApplication(), pin)
        _state.value = AuthState.Success
    }
}
