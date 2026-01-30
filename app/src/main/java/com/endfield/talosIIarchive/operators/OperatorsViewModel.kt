package com.endfield.talosIIarchive.operators

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endfield.talosIIarchive.data.model.Operator
import com.endfield.talosIIarchive.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OperatorsViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService

    private val _uiState = MutableStateFlow(OperatorsUiState())
    val uiState: StateFlow<OperatorsUiState> = _uiState.asStateFlow()

    init {
        loadOperators()
    }

    private fun loadOperators() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val operators = apiService.getOperators()
                _uiState.update {
                    it.copy(
                        operators = operators,
                        filteredOperators = operators,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error loading operators"
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { currentState ->
            val filtered = if (query.isBlank()) {
                currentState.operators
            } else {
                currentState.operators.filter { operator ->
                    operator.name.contains(query, ignoreCase = true) ||
                            operator.element.contains(query, ignoreCase = true) ||
                            operator.weaponType.contains(query, ignoreCase = true) ||
                            operator.operatorClass.contains(query, ignoreCase = true)
                }
            }

            currentState.copy(
                searchQuery = query,
                filteredOperators = filtered
            )
        }
    }
}

data class OperatorsUiState(
    val operators: List<Operator> = emptyList(),
    val filteredOperators: List<Operator> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)