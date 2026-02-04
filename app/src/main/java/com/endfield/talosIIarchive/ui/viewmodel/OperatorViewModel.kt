package com.endfield.talosIIarchive.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endfield.talosIIarchive.domain.models.Operator
import com.endfield.talosIIarchive.domain.models.Weapon
import com.endfield.talosIIarchive.domain.repositoty.OperatorRepository
import kotlinx.coroutines.launch

class OperatorViewModel(private val repository: OperatorRepository) : ViewModel() {

    var operators by mutableStateOf<List<Operator>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set


    fun fetchOperators() {
        if (operators.isNotEmpty()) return

        viewModelScope.launch {
            startLoading()
            try {
                operators = repository.getAllOperators()
                if (operators.isEmpty()) errorMessage = "No se encontraron operadores."
            } catch (e: Exception) {
                errorMessage = "Error al conectar: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }



    private fun startLoading() {
        isLoading = true
        errorMessage = null
    }

    // Función para "pestañear" o limpiar errores desde la UI
    fun clearError() {
        errorMessage = null
    }

    var selectedOperatorFull by mutableStateOf<Operator?>(null)
        private set

    var isDetailLoading by mutableStateOf(false)
        private set

    fun fetchOperatorDetails(id: Int) {
        viewModelScope.launch {
            isDetailLoading = true
            val result = repository.getOperatorById(id)
            selectedOperatorFull = result
            isDetailLoading = false
        }
    }

    fun clearSelectedOperator() {
        selectedOperatorFull = null
    }

}