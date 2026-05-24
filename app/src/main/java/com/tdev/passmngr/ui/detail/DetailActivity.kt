package com.tdev.passmngr.ui.detail

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tdev.passmngr.PassMngrApp
import com.tdev.passmngr.R
import com.tdev.passmngr.databinding.ActivityDetailBinding
import com.tdev.passmngr.ui.add.AddEditActivity
import com.tdev.passmngr.util.ClipboardUtil
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getLongExtra("id", -1L)
        val repo = (application as PassMngrApp).repository

        lifecycleScope.launch {
            val p = repo.getById(id) ?: run { finish(); return@launch }
            val plain = repo.decryptPassword(p)

            binding.tvAccount.text = p.accountName
            binding.tvUsername.text = p.username
            binding.tvCategory.text = p.category.label
            binding.tvPassword.text = "•".repeat(plain.length)

            var visible = false
            binding.btnToggle.setOnClickListener {
                visible = !visible
                binding.tvPassword.text = if (visible) plain else "•".repeat(plain.length)
                binding.btnToggle.setImageResource(if (visible) R.drawable.ic_eye_off else R.drawable.ic_eye)
            }

            binding.btnCopyUsername.setOnClickListener {
                ClipboardUtil.copy(this@DetailActivity, "Kullanıcı adı", p.username)
            }

            binding.btnCopyPassword.setOnClickListener {
                ClipboardUtil.copy(this@DetailActivity, "Şifre", plain)
            }

            binding.btnEdit.setOnClickListener {
                startActivity(Intent(this@DetailActivity, AddEditActivity::class.java).putExtra("id", p.id))
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
