package com.smartkargo.myapplication.presentation.screen.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkargo.myapplication.domain.model.Category
import com.smartkargo.myapplication.domain.model.Transaction
import com.smartkargo.myapplication.domain.model.TransactionType
import com.smartkargo.myapplication.domain.usecase.AddTransactionUseCase
import com.smartkargo.myapplication.domain.usecase.UpdateTransactionUseCase
import com.smartkargo.myapplication.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionFormState(
    val amount: String = "",
    val category: String = Category.OTHER.displayName,
    val note: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val date: Long = System.currentTimeMillis(),
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val repository: TransactionRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(TransactionFormState())
    val formState: StateFlow<TransactionFormState> = _formState.asStateFlow()

    fun loadTransaction(id: Long) {
        viewModelScope.launch {
            repository.getTransactionById(id)?.let { t ->
                _formState.update {
                    it.copy(
                        amount = t.amount.toString(),
                        category = t.category,
                        note = t.note,
                        type = t.type,
                        date = t.date
                    )
                }
            }
        }
    }

    fun updateAmount(value: String) { _formState.update { it.copy(amount = value) } }
    fun updateCategory(value: String) { _formState.update { it.copy(category = value) } }
    fun updateNote(value: String) { _formState.update { it.copy(note = value) } }
    fun updateType(value: TransactionType) { _formState.update { it.copy(type = value) } }
    fun updateDate(value: Long) { _formState.update { it.copy(date = value) } }

    fun saveTransaction(existingId: Long? = null) {
        val state = _formState.value
        val amount = state.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _formState.update { it.copy(error = "Enter a valid amount") }
            return
        }
        viewModelScope.launch {
            val transaction = Transaction(
                id = existingId ?: 0,
                amount = amount,
                category = state.category,
                note = state.note,
                type = state.type,
                date = state.date
            )
            if (existingId != null) {
                updateTransactionUseCase(transaction)
            } else {
                addTransactionUseCase(transaction)
            }
            _formState.update { it.copy(isSaved = true) }
        }
    }
}
