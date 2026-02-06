package com.endfield.talosIIarchive.domain.repositoty

import com.endfield.talosIIarchive.domain.models.Blueprint

interface BlueprintRepository {
    suspend fun getAllBlueprints(): List<Blueprint>
}