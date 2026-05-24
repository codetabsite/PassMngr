package com.tdev.passmngr.ui.home

import androidx.lifecycle.*
import com.tdev.passmngr.data.model.Category
import com.tdev.passmngr.data.model.Password
import com.tdev.passmngr.data.repository.PasswordRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: PasswordRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _category = MutableStateFlow<Category?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val passwords: StateFlow<List<Password>> = combine(_query, _category) { q, cat ->
        Pair(q, cat)
    }.flatMapLatest { (q, cat) ->
        when {
            cat != null -> repo.getByCategory(cat)
            q.isNotBlank() -> repo.search(q)
            else -> repo.getAll()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(query: String) { _query.value = query }
    fun setCategory(category: Category?) { _category.value = category }

    fun delete(password: Password) = viewModelScope.launch { repo.delete(password) }

    fun decryptPassword(password: Password): String = repo.decryptPassword(password)
}

class HomeViewModelFactory(private val repo: PasswordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(repo) as T
    }
}
