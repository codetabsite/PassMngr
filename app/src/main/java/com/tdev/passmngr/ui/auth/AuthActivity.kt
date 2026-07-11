package com.tdev.passmngr.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.tdev.passmngr.PassMngrApp
import com.tdev.passmngr.databinding.ActivityAuthBinding
import com.tdev.passmngr.ui.home.HomeActivity
import com.tdev.passmngr.util.PinManager
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is AuthState.BiometricPrompt -> showBiometric()
                    is AuthState.PinEntry        -> showPinUi()
                    is AuthState.Success         -> openHome()
                    is AuthState.Error           -> Toast.makeText(this@AuthActivity, state.message, Toast.LENGTH_SHORT).show()
                    is AuthState.Idle            -> Unit
                }
            }
        }

        binding.btnAuth.setOnClickListener {
            val pin = binding.etPin.text?.toString().orEmpty()
            if (pin.length < 4) {
                Toast.makeText(this, "En az 4 haneli PIN gir", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!viewModel.pinIsSet) {
                viewModel.setPin(pin)
            } else {
                viewModel.checkPin(pin)
            }
        }

        viewModel.startAuth()
    }

    private fun showBiometric() {
        val canAuth = BiometricManager.from(this)
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
            viewModel.onBiometricUnavailable()
            return
        }
        val prompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    viewModel.onBiometricSuccess()
                }
                override fun onAuthenticationError(code: Int, msg: CharSequence) {
                    if (code != BiometricPrompt.ERROR_USER_CANCELED &&
                        code != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        viewModel.onBiometricUnavailable()
                    }
                }
                override fun onAuthenticationFailed() = Unit
            }
        )
        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("PassMngr")
                .setSubtitle("Kimliğinizi doğrulayın")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .setNegativeButtonText("PIN ile giriş")
                .build()
        )
    }

    private fun showPinUi() {
        binding.etPin.visibility = android.view.View.VISIBLE
        binding.btnAuth.text = if (!viewModel.pinIsSet) "PIN Oluştur" else "Giriş"
        binding.tvSubtitle.text = if (!viewModel.pinIsSet) "Bir PIN oluşturun" else "PIN girin"
    }

    private fun openHome() {
        (application as PassMngrApp).isUnlocked = true
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
