package com.tdev.passmngr.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
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
            val plain = repo.decrypt(p)

            binding.tvAccount.text = p.accountName
            binding.tvUsername.text = p.username
            binding.tvCategory.text = p.category.label
            binding.tvPassword.text = "•".repeat(minOf(plain.length, 24))

            // Not alanı — boşsa gizle
            if (p.note.isNotBlank()) {
                binding.tvNoteLabel.visibility = View.VISIBLE
                binding.tvNote.visibility = View.VISIBLE
                binding.tvNote.text = p.note
            } else {
                binding.tvNoteLabel.visibility = View.GONE
                binding.tvNote.visibility = View.GONE
            }

            var visible = false
            binding.btnToggle.setOnClickListener {
                visible = !visible
                binding.tvPassword.text = if (visible) plain else "•".repeat(minOf(plain.length, 24))
                binding.btnToggle.setImageResource(
                    if (visible) R.drawable.ic_eye_off else R.drawable.ic_eye
                )
            }

            binding.btnCopyUsername.setOnClickListener {
                ClipboardUtil.copy(this@DetailActivity, "Kullanıcı adı", p.username)
            }

            binding.btnCopyPassword.setOnClickListener {
                ClipboardUtil.copy(this@DetailActivity, "Şifre", plain)
            }

            binding.btnHistory.setOnClickListener {
                showHistory(id, repo)
            }

            binding.btnEdit.setOnClickListener {
                startActivity(
                    Intent(this@DetailActivity, AddEditActivity::class.java).putExtra("id", p.id)
                )
                finish()
            }
        }
    }

    private fun showHistory(id: Long, repo: com.tdev.passmngr.data.repository.PasswordRepository) {
        lifecycleScope.launch {
            val history = repo.getHistory(id)
            if (history.isEmpty()) {
                AlertDialog.Builder(this@DetailActivity)
                    .setTitle("Şifre Geçmişi")
                    .setMessage("Henüz geçmiş yok.")
                    .setPositiveButton("Tamam", null)
                    .show()
                return@launch
            }
            AlertDialog.Builder(this@DetailActivity)
                .setTitle("Şifre Geçmişi (son ${history.size})")
                .setItems(history.toTypedArray(), null)
                .setPositiveButton("Tamam", null)
                .show()
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
