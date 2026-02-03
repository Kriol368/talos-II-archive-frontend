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

    // --- ESTADOS DE PERSONAJES ---
    var operators by mutableStateOf<List<Operator>>(emptyList())
        private set

    // --- ESTADOS DE ARMAS ---
    var weapons by mutableStateOf<List<Weapon>>(emptyList())
        private set

    // --- ESTADOS GLOBALES ---
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    /**
     * Carga los operadores solo si la lista está vacía (evita peticiones extra)
     */
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

    /**
     * Carga las armas desde el repositorio
     */
    fun fetchWeapons() {
        if (weapons.isNotEmpty()) return

        viewModelScope.launch {
            startLoading()
            try {
                weapons = repository.getAllWeapons()
                if (weapons.isEmpty()) errorMessage = "No se encontraron armas."
            } catch (e: Exception) {
                errorMessage = "Error al cargar armas: ${e.localizedMessage}"
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
}