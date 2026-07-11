package com.tdev.passmngr.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.tdev.passmngr.PassMngrApp
import com.tdev.passmngr.R
import com.tdev.passmngr.data.model.Category
import com.tdev.passmngr.data.model.Password
import com.tdev.passmngr.data.model.SortOrder
import com.tdev.passmngr.databinding.ActivityHomeBinding
import com.tdev.passmngr.ui.add.AddEditActivity
import com.tdev.passmngr.ui.auth.AuthActivity
import com.tdev.passmngr.ui.detail.DetailActivity
import com.tdev.passmngr.util.ClipboardUtil
import com.tdev.passmngr.util.ExportManager
import com.tdev.passmngr.util.PinManager
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((application as PassMngrApp).repository)
    }
    private lateinit var adapter: PasswordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        adapter = PasswordAdapter(
            onCopy = { p ->
                val plain = viewModel.decryptPassword(p)
                ClipboardUtil.copy(this, p.accountName, plain)
                viewModel.markUsed(p.id)
            },
            onClick = { p ->
                viewModel.markUsed(p.id)
                startActivity(Intent(this, DetailActivity::class.java).putExtra("id", p.id))
            },
            onDelete = { p -> confirmDelete(p) }
        )
        binding.recycler.adapter = adapter

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                adapter.submitList(state.passwords)
                binding.tvEmpty.visibility =
                    if (state.passwords.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }
        }

        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddEditActivity::class.java))
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setQuery(newText.orEmpty())
                return true
            }
        })

        binding.chipAll.setOnClickListener    { viewModel.setCategory(null) }
        binding.chipSocial.setOnClickListener { viewModel.setCategory(Category.SOCIAL) }
        binding.chipBank.setOnClickListener   { viewModel.setCategory(Category.BANK) }
        binding.chipGame.setOnClickListener   { viewModel.setCategory(Category.GAME) }
        binding.chipEmail.setOnClickListener  { viewModel.setCategory(Category.EMAIL) }
        binding.chipWork.setOnClickListener   { viewModel.setCategory(Category.WORK) }
        binding.chipOther.setOnClickListener  { viewModel.setCategory(Category.OTHER) }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_export -> { exportPasswords(); true }
                R.id.action_sort_name -> { viewModel.setSort(SortOrder.NAME_ASC); true }
                R.id.action_sort_date -> { viewModel.setSort(SortOrder.DATE_DESC); true }
                R.id.action_sort_used -> { viewModel.setSort(SortOrder.LAST_USED); true }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Otomatik kilit kontrolü
        val app = application as PassMngrApp
        val elapsed = System.currentTimeMillis() - app.backgroundedAt
        if (app.backgroundedAt > 0 && elapsed > PinManager.getAutoLockMillis(this)) {
            app.isUnlocked = false
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        (application as PassMngrApp).backgroundedAt = System.currentTimeMillis()
    }

    private fun confirmDelete(password: Password) {
        AlertDialog.Builder(this)
            .setTitle("Sil")
            .setMessage("${password.accountName} silinsin mi?")
            .setPositiveButton("Sil") { _, _ -> viewModel.delete(password) }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun exportPasswords() {
        lifecycleScope.launch {
            val list = viewModel.uiState.value.passwords
            if (list.isEmpty()) return@launch
            startActivity(
                Intent.createChooser(
                    ExportManager.exportToCsv(this@HomeActivity, list), "Yedek paylaş"
                )
            )
        }
    }
}
