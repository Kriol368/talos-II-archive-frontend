package com.endfield.talosIIarchive.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endfield.talosIIarchive.domain.models.Blueprint
import com.endfield.talosIIarchive.domain.repositoty.BlueprintRepository
import kotlinx.coroutines.launch

class BlueprintViewModel(private val repository: BlueprintRepository) : ViewModel() {
    var blueprints by mutableStateOf<List<Blueprint>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchBlueprints() {
        viewModelScope.launch {
            startLoading()
            try {
                blueprints = repository.getAllBlueprints()
                if (blueprints.isEmpty()) {
                    errorMessage = "No blueprints found"
                }
            } catch (e: Exception) {
                errorMessage = "Error loading blueprints: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    suspend fun createBlueprint(title: String, description: String, codeHash: String): Boolean {
        return try {
            val newBlueprint = repository.createBlueprint(title, description, codeHash)
            if (newBlueprint != null) {
                fetchBlueprints()
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun startLoading() {
        isLoading = true
        errorMessage = null
    }

}