package com.endfield.talosIIarchive.domain.repositoty

import android.util.Log
import com.endfield.talosIIarchive.api.client
import com.endfield.talosIIarchive.domain.models.Blueprint
import io.ktor.client.call.body
import io.ktor.client.request.get

class BlueprintRepositoryImpl : BlueprintRepository {
    override suspend fun getAllBlueprints(): List<Blueprint> {
        return try {
            client.get("endfield/blueprints").body()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error fetching blueprints: ${e.message}")
            emptyList()
        }
    }
}