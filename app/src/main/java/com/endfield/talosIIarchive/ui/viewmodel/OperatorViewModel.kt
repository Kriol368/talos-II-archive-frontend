package com.endfield.talosIIarchive.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endfield.talosIIarchive.domain.models.Operator
import com.endfield.talosIIarchive.domain.repositoty.OperatorRepository
import kotlinx.coroutines.launch

class OperatorViewModel(private val repository: OperatorRepository) : ViewModel() {

    // Estado de la UI: Lista vacía al inicio
    var operators by mutableStateOf<List<Operator>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun fetchOperators() {
        viewModelScope.launch {
            isLoading = true
            operators = repository.getAllOperators()
            isLoading = false
        }
    }
}