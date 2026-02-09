package com.endfield.talosIIarchive.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endfield.talosIIarchive.domain.models.Gear
import com.endfield.talosIIarchive.domain.models.Weapon
import com.endfield.talosIIarchive.domain.repositoty.GearRepository
import kotlinx.coroutines.launch

class GearViewModel(private val repository: GearRepository) : ViewModel() {
    var gearList by mutableStateOf<List<Gear>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set


    var selectedGearFull by mutableStateOf<Gear?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    var selectedGear by mutableStateOf<Gear?>(null)
        private set

    var isDetailLoading by mutableStateOf(false)
        private set

    fun fetchGear() {
        if (gearList.isNotEmpty()) return

        viewModelScope.launch {
            startLoading()
            try {
                gearList = repository.getAllGear()
                if (gearList.isEmpty()) {
                    errorMessage = "No gear found in database"
                }
            } catch (e: Exception) {
                errorMessage = "Error loading gear: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchGearDetails(id: Int) {
        viewModelScope.launch {
            isDetailLoading = true
            try {
                selectedGear = repository.getGearById(id)
            } catch (e: Exception) {
                errorMessage = "Error loading gear details: ${e.localizedMessage}"
            } finally {
                isDetailLoading = false
            }
        }
    }

    fun clearSelectedGear() {
        selectedGear = null
    }

    private fun startLoading() {
        isLoading = true
        errorMessage = null
    }

    fun clearError() {
        errorMessage = null
    }
}