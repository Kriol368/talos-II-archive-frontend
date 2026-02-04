package com.endfield.talosIIarchive.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endfield.talosIIarchive.domain.models.Weapon
import com.endfield.talosIIarchive.domain.repositoty.WeaponRepository
import kotlinx.coroutines.launch

class WeaponViewModel(private val repository: WeaponRepository) : ViewModel() {
    var weapons by mutableStateOf<List<Weapon>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchWeapons() {
        if (weapons.isNotEmpty()) return

        viewModelScope.launch {
            startLoading()
            try {
                weapons = repository.getAllWeapons()
                if (weapons.isEmpty()) {
                    errorMessage = "No weapons found in database"
                }
            } catch (e: Exception) {
                errorMessage = "Error loading weapons: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun startLoading() {
        isLoading = true
        errorMessage = null
    }

    fun clearError() {
        errorMessage = null
    }
}