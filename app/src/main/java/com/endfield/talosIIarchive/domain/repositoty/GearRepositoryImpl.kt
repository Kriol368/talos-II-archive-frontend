package com.endfield.talosIIarchive.domain.repositoty

import android.util.Log
import com.endfield.talosIIarchive.api.client
import com.endfield.talosIIarchive.domain.models.Gear
import io.ktor.client.call.body
import io.ktor.client.request.get

class GearRepositoryImpl : GearRepository {
    override suspend fun getAllGear(): List<Gear> {
        return try {
            client.get("endfield/gear").body()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error fetching gear: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getGearById(id: Int): Gear? {
        return try {
            client.get("endfield/gear/$id").body()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error fetching gear $id: ${e.message}")
            null
        }
    }
}