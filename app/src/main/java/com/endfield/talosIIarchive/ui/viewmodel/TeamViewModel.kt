package com.endfield.talosIIarchive.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endfield.talosIIarchive.domain.models.CreateTeamRequest
import com.endfield.talosIIarchive.domain.models.Team
import com.endfield.talosIIarchive.domain.models.TeamDetail
import com.endfield.talosIIarchive.domain.repositoty.TeamRepository
import kotlinx.coroutines.launch

class TeamViewModel(private val repository: TeamRepository) : ViewModel() {
    var teams by mutableStateOf<List<Team>>(emptyList())
        private set

    var selectedTeam by mutableStateOf<TeamDetail?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isDetailLoading by mutableStateOf(false)
        private set

    var isCreating by mutableStateOf(false)  // Add this
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchTeams() {
        if (teams.isNotEmpty()) return

        viewModelScope.launch {
            startLoading()
            try {
                teams = repository.getAllTeams()
                if (teams.isEmpty()) {
                    errorMessage = "No teams found"
                }
            } catch (e: Exception) {
                errorMessage = "Error loading teams: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchTeamDetails(id: Int) {
        viewModelScope.launch {
            isDetailLoading = true
            try {
                selectedTeam = repository.getTeamById(id)
            } catch (e: Exception) {
                errorMessage = "Error loading team details: ${e.localizedMessage}"
            } finally {
                isDetailLoading = false
            }
        }
    }

    suspend fun createTeam(request: CreateTeamRequest): Boolean {  // Make this suspend
        isCreating = true
        return try {
            val newTeam = repository.createTeam(request)
            if (newTeam != null) {
                fetchTeams()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            errorMessage = "Error creating team: ${e.localizedMessage}"
            false
        } finally {
            isCreating = false
        }
    }

    fun clearSelectedTeam() {
        selectedTeam = null
    }

    private fun startLoading() {
        isLoading = true
        errorMessage = null
    }

    fun clearError() {
        errorMessage = null
    }
}