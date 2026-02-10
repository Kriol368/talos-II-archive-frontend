package com.endfield.talosIIarchive.domain.repositoty

import android.util.Log
import com.endfield.talosIIarchive.api.client
import com.endfield.talosIIarchive.domain.models.Weapon
import io.ktor.client.call.body
import io.ktor.client.request.get

class WeaponRepositoryImpl : WeaponRepository {
    override suspend fun getAllWeapons(): List<Weapon> {
        return try {
            client.get("endfield/weapons").body()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error fetching weapons: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getWeaponById(id: Int): Weapon? {
        return try {
            client.get("endfield/weapons/$id").body()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error fetching weapon $id: ${e.message}")
            null
        }
    }
}