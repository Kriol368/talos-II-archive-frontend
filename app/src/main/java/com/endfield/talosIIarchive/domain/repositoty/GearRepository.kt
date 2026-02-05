package com.endfield.talosIIarchive.domain.repositoty

import com.endfield.talosIIarchive.domain.models.Gear

interface GearRepository {
    suspend fun getAllGear(): List<Gear>
    suspend fun getGearById(id: Int): Gear?
}