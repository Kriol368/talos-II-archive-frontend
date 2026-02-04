package com.endfield.talosIIarchive.domain.repositoty

import android.util.Log
import com.endfield.talosIIarchive.api.client
import com.endfield.talosIIarchive.domain.models.Weapon
import io.ktor.client.call.body
import io.ktor.client.request.get

class WeaponRepositoryImpl : WeaponRepository {
    override suspend fun getAllWeapons(): List<Weapon> {
        return try {
            client.get("weapons").body()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error lista: ${e.message}")
            emptyList()
        }
    }
}