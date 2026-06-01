package com.smartkargo.myapplication.presentation.screen.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkargo.myapplication.domain.usecase.ExportTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    exportTransactionsUseCase: ExportTransactionsUseCase
) : ViewModel() {

    private val cal = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
    private val startOfMonth = cal.timeInMillis
    private val endOfMonth = cal.apply { add(Calendar.MONTH, 1) }.timeInMillis

    val csvData: StateFlow<String> = exportTransactionsUseCase(startOfMonth, endOfMonth)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
}

