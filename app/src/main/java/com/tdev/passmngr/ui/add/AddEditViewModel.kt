package com.tdev.passmngr.ui.add

import androidx.lifecycle.*
import com.tdev.passmngr.data.model.Category
import com.tdev.passmngr.data.model.Password
import com.tdev.passmngr.data.repository.PasswordRepository
import com.tdev.passmngr.util.PasswordGenerator
import kotlinx.coroutines.launch

class AddEditViewModel(private val repo: PasswordRepository) : ViewModel() {

    private val _saved = MutableLiveData(false)
    val saved: LiveData<Boolean> = _saved

    var existing: Password? = null

    fun load(id: Long) = viewModelScope.launch {
        existing = repo.getById(id)
    }

    fun decryptExisting(): String = existing?.let { repo.decryptPassword(it) } ?: ""

    fun generatePassword(): String = PasswordGenerator.generate()

    fun getStrength(pw: String): Int = PasswordGenerator.strength(pw)

    fun save(account: String, username: String, password: String, category: Category) = viewModelScope.launch {
        val ex = existing
        if (ex == null) {
            repo.save(account, username, password, category)
        } else {
            repo.update(ex, account, username, password, category)
        }
        _saved.postValue(true)
    }
}

class AddEditViewModelFactory(private val repo: PasswordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AddEditViewModel(repo) as T
    }
}
