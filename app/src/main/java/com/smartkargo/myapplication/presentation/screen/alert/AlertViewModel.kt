package com.smartkargo.myapplication.presentation.screen.alert
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkargo.myapplication.domain.model.ExpenseAlert
import com.smartkargo.myapplication.domain.model.RepeatType
import com.smartkargo.myapplication.domain.repository.ExpenseAlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
data class AlertUiState(
    val allAlerts: List<ExpenseAlert> = emptyList(),
    val todayAlerts: List<ExpenseAlert> = emptyList(),
    val upcomingAlerts: List<ExpenseAlert> = emptyList(),
    val isLoading: Boolean = true
)
data class AlertFormState(
    val title: String = "",
    val amount: String = "",
    val dueDate: Long = System.currentTimeMillis(),
    val repeatType: RepeatType = RepeatType.NONE,
    val isSaved: Boolean = false,
    val error: String? = null
)
@HiltViewModel
class AlertViewModel @Inject constructor(
    private val repository: ExpenseAlertRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AlertUiState())
    val uiState: StateFlow<AlertUiState> = _uiState.asStateFlow()
    private val _formState = MutableStateFlow(AlertFormState())
    val formState: StateFlow<AlertFormState> = _formState.asStateFlow()
    init {
        viewModelScope.launch {
            combine(
                repository.getAllAlerts(),
                repository.getAlertsDueToday(),
                repository.getUpcomingAlerts(7)
            ) { all, today, upcoming ->
                AlertUiState(
                    allAlerts = all,
                    todayAlerts = today,
                    upcomingAlerts = upcoming,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }
    fun updateTitle(value: String) { _formState.update { it.copy(title = value) } }
    fun updateAmount(value: String) { _formState.update { it.copy(amount = value) } }
    fun updateDueDate(value: Long) { _formState.update { it.copy(dueDate = value) } }
    fun updateRepeatType(value: RepeatType) { _formState.update { it.copy(repeatType = value) } }
    fun saveAlert() {
        val state = _formState.value
        if (state.title.isBlank()) {
            _formState.update { it.copy(error = "Title is required") }
            return
        }
        val amount = state.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _formState.update { it.copy(error = "Enter a valid amount") }
            return
        }
        viewModelScope.launch {
            repository.insertAlert(
                ExpenseAlert(
                    title = state.title,
                    amount = amount,
                    dueDate = state.dueDate,
                    repeatType = state.repeatType
                )
            )
            _formState.update { it.copy(isSaved = true) }
        }
    }
    fun deleteAlert(id: Long) {
        viewModelScope.launch { repository.deleteAlert(id) }
    }
    fun toggleAlert(alert: ExpenseAlert) {
        viewModelScope.launch {
            repository.updateAlert(alert.copy(isEnabled = !alert.isEnabled))
        }
    }
    fun resetForm() {
        _formState.value = AlertFormState()
    }
}
