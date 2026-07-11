package com.tdev.passmngr.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tdev.passmngr.data.model.Category
import com.tdev.passmngr.data.model.Password
import com.tdev.passmngr.data.model.SortOrder
import com.tdev.passmngr.data.repository.PasswordRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val passwords: List<Password> = emptyList(),
    val query: String = "",
    val category: Category? = null,
    val sort: SortOrder = SortOrder.NAME_ASC,
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(private val repo: PasswordRepository) : ViewModel() {

    private val _sort = MutableStateFlow(SortOrder.NAME_ASC)
    private val _query = MutableStateFlow("")
    private val _category = MutableStateFlow<Category?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        _sort.flatMapLatest { repo.getAll(it) },
        _query,
        _category,
        _sort,
    ) { all, query, category, sort ->
        val filtered = all
            .filter { p ->
                (query.isBlank() || p.accountName.contains(query, ignoreCase = true) ||
                    p.username.contains(query, ignoreCase = true))
                && (category == null || p.category == category)
            }
        HomeUiState(passwords = filtered, query = query, category = category, sort = sort)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun setQuery(q: String) { _query.value = q }
    fun setCategory(c: Category?) { _category.value = c }
    fun setSort(s: SortOrder) { _sort.value = s }
    fun delete(p: Password) = viewModelScope.launch { repo.delete(p) }
    fun decryptPassword(p: Password): String = repo.decrypt(p)
    fun markUsed(id: Long) = viewModelScope.launch { repo.markUsed(id) }
}

class HomeViewModelFactory(private val repo: PasswordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(repo) as T
    }
}
