package com.endfield.talosIIarchive.domain.repositoty

import android.util.Log
import com.endfield.talosIIarchive.api.client
import com.endfield.talosIIarchive.domain.models.Blueprint
import com.endfield.talosIIarchive.domain.models.BlueprintRequest
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class BlueprintRepositoryImpl : BlueprintRepository {
    override suspend fun getAllBlueprints(): List<Blueprint> {
        return try {
            client.get("endfield/blueprints").body()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error fetching blueprints: ${e.message}")
            emptyList()
        }
    }

    override suspend fun createBlueprint(
        title: String,
        description: String,
        codeHash: String
    ): Blueprint? {
        return try {

            val requestBody = BlueprintRequest(
                title = title,
                description = description,
                codeHash = codeHash
            )

            client.post("endfield/blueprints") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body<Blueprint>()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error creating blueprint: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}