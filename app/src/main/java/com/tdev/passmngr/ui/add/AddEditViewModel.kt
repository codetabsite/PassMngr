package com.tdev.passmngr.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tdev.passmngr.data.model.Category
import com.tdev.passmngr.data.model.Password
import com.tdev.passmngr.data.repository.PasswordRepository
import com.tdev.passmngr.util.PasswordGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditViewModel(private val repo: PasswordRepository) : ViewModel() {

    private val _saved = MutableStateFlow(false)
    val saved = _saved.asStateFlow()

    var existing: Password? = null
        private set

    fun load(id: Long) = viewModelScope.launch {
        existing = repo.getById(id)
    }

    fun decryptExisting(): String = existing?.let { repo.decrypt(it) } ?: ""

    fun generatePassword(): String = PasswordGenerator.generate()

    fun getStrength(pw: String): Int = PasswordGenerator.strength(pw)

    fun save(
        accountName: String,
        username: String,
        password: String,
        category: Category,
        note: String,
    ) = viewModelScope.launch {
        repo.save(
            Password(
                id = existing?.id ?: 0L,
                accountName = accountName,
                username = username,
                encryptedPassword = password,
                category = category,
                note = note,
                createdAt = existing?.createdAt ?: System.currentTimeMillis(),
            )
        )
        _saved.value = true
    }
}

class AddEditViewModelFactory(private val repo: PasswordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AddEditViewModel(repo) as T
    }
}
