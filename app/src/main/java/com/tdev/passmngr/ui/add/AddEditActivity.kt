package com.tdev.passmngr.ui.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tdev.passmngr.PassMngrApp
import com.tdev.passmngr.R
import com.tdev.passmngr.data.model.Category
import com.tdev.passmngr.databinding.ActivityAddEditBinding
import kotlinx.coroutines.launch

class AddEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditBinding
    private val viewModel: AddEditViewModel by viewModels {
        AddEditViewModelFactory((application as PassMngrApp).repository)
    }

    private val categories = Category.entries.toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.spinnerCategory.adapter = android.widget.ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            categories.map { it.label }
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        val editId = intent.getLongExtra("id", -1L)
        if (editId != -1L) {
            supportActionBar?.title = "Düzenle"
            lifecycleScope.launch {
                viewModel.load(editId)
                viewModel.existing?.let { p ->
                    binding.etAccount.setText(p.accountName)
                    binding.etUsername.setText(p.username)
                    binding.etPassword.setText(viewModel.decryptExisting())
                    binding.etNote.setText(p.note)
                    binding.spinnerCategory.setSelection(categories.indexOf(p.category))
                }
            }
        }

        binding.btnGenerate.setOnClickListener {
            binding.etPassword.setText(viewModel.generatePassword())
        }

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { updateStrengthBar(s.toString()) }
        })

        binding.btnTogglePassword.setOnClickListener { togglePasswordVisibility() }

        binding.btnSave.setOnClickListener {
            val account  = binding.etAccount.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val note     = binding.etNote.text.toString().trim()
            val category = categories[binding.spinnerCategory.selectedItemPosition]

            if (account.isEmpty()) {
                binding.tilAccount.error = "Boş bırakılamaz"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.tilPassword.error = "Boş bırakılamaz"
                return@setOnClickListener
            }
            binding.tilAccount.error = null
            binding.tilPassword.error = null

            viewModel.save(account, username, password, category, note)
        }

        lifecycleScope.launch {
            viewModel.saved.collect { if (it) finish() }
        }
    }

    private fun updateStrengthBar(pw: String) {
        val strength = viewModel.getStrength(pw)
        binding.strengthBar.progress = strength * 20
        val (color, label) = when (strength) {
            1    -> R.color.strength_weak to "Çok zayıf"
            2    -> R.color.strength_fair to "Zayıf"
            3    -> R.color.strength_good to "İyi"
            4    -> R.color.strength_strong to "Güçlü"
            5    -> R.color.strength_very_strong to "Çok güçlü"
            else -> R.color.strength_weak to ""
        }
        binding.strengthBar.progressTintList =
            android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(this, color)
            )
        binding.tvStrength.text = label
    }

    private fun togglePasswordVisibility() {
        val isVisible = binding.etPassword.transformationMethod == null
        binding.etPassword.transformationMethod = if (isVisible)
            android.text.method.PasswordTransformationMethod.getInstance()
        else
            null
        binding.btnTogglePassword.setImageResource(
            if (isVisible) R.drawable.ic_eye_off else R.drawable.ic_eye
        )
        binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
