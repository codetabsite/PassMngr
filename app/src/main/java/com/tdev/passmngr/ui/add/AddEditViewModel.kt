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

    private val _existing = MutableStateFlow<Password?>(null)

    fun load(id: Long) = viewModelScope.launch {
        _existing.value = repo.getById(id)
    }

    fun getExisting(): Password? = _existing.value

    fun generatePassword(): String = PasswordGenerator.generate()

    fun save(
        accountName: String,
        username: String,
        password: String,
        category: Category,
        note: String,
        onDone: () -> Unit,
    ) = viewModelScope.launch {
        val existing = _existing.value
        repo.save(
            Password(
                id = existing?.id ?: 0L,
                accountName = accountName.trim(),
                username = username.trim(),
                encryptedPassword = password,
                category = category,
                note = note.trim(),
                createdAt = existing?.createdAt ?: System.currentTimeMillis(),
            )
        )
        onDone()
    }

    fun getHistory(id: Long, onResult: (List<String>) -> Unit) = viewModelScope.launch {
        onResult(repo.getHistory(id))
    }
}

class AddEditViewModelFactory(private val repo: PasswordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AddEditViewModel(repo) as T
    }
}
