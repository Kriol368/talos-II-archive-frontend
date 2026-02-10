package com.endfield.talosIIarchive.domain.repositoty

import com.endfield.talosIIarchive.domain.models.Blueprint

interface BlueprintRepository {
    suspend fun getAllBlueprints(): List<Blueprint>
    suspend fun createBlueprint(title: String, description: String, codeHash: String): Blueprint?
}