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

    var operators by mutableStateOf<List<Operator>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun updateOrder(newList: List<Operator>) {
        operators = newList
    }

    var errorMessage by mutableStateOf<String?>(null)
        private set


    fun fetchOperators() {
        if (operators.isNotEmpty()) return

        viewModelScope.launch {
            startLoading()
            try {
                operators = repository.getAllOperators()
                if (operators.isEmpty()) errorMessage = "No operators found."
            } catch (e: Exception) {
                errorMessage = "Error connecting: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }


    private fun startLoading() {
        isLoading = true
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