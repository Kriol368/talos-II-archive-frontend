package com.endfield.talosIIarchive.domain.repositoty

import com.endfield.talosIIarchive.domain.models.CreateTeamRequest
import com.endfield.talosIIarchive.domain.models.Team
import com.endfield.talosIIarchive.domain.models.TeamDetail

interface TeamRepository {
    suspend fun getAllTeams(): List<Team>
    suspend fun getTeamById(id: Int): TeamDetail?
    suspend fun createTeam(request: CreateTeamRequest): Team?
}