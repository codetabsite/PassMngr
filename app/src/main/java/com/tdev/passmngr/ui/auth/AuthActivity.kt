package com.tdev.passmngr.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tdev.passmngr.databinding.ActivityAuthBinding
import com.tdev.passmngr.ui.home.HomeActivity

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authenticate()
        binding.btnAuth.setOnClickListener { authenticate() }
    }

    private fun authenticate() {
        if (!viewModel.isBiometricAvailable(this)) {
            openHome()
            return
        }
        val prompt = viewModel.buildPrompt(
            activity = this,
            onSuccess = { openHome() },
            onError = { msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
        )
        prompt.authenticate(viewModel.promptInfo())
    }

    private fun openHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
