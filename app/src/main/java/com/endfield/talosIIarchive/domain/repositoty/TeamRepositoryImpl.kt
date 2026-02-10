package com.endfield.talosIIarchive.domain.repositoty

import android.util.Log
import com.endfield.talosIIarchive.api.client
import com.endfield.talosIIarchive.domain.models.CreateTeamRequest
import com.endfield.talosIIarchive.domain.models.Team
import com.endfield.talosIIarchive.domain.models.TeamDetail
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TeamRepositoryImpl : TeamRepository {
    override suspend fun getAllTeams(): List<Team> {
        return try {
            client.get("endfield/teams").body()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error fetching teams: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getTeamById(id: Int): TeamDetail? {
        return try {
            client.get("endfield/teams/$id").body()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error fetching team $id: ${e.message}")
            null
        }
    }

    override suspend fun createTeam(request: CreateTeamRequest): Team? {
        return try {
            client.post("endfield/teams") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<Team>()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error creating team: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}