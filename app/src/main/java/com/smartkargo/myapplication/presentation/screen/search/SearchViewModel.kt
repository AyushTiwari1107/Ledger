package com.smartkargo.myapplication.presentation.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkargo.myapplication.domain.model.Transaction
import com.smartkargo.myapplication.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val selectedCategory: String? = null,
    val results: List<Transaction> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getTransactions: GetTransactionsUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _category = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SearchUiState> = combine(_query, _category) { q, c -> Pair(q, c) }
        .flatMapLatest { (query, category) ->
            when {
                query.isNotBlank() -> getTransactions.search(query)
                category != null -> getTransactions.byCategory(category)
                else -> getTransactions()
            }.map { SearchUiState(query, category, it) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchUiState())

    fun updateQuery(query: String) { _query.value = query }
    fun updateCategory(category: String?) { _category.value = category }
}

