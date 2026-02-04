package com.endfield.talosIIarchive.domain.repositoty

import com.endfield.talosIIarchive.domain.models.Weapon

interface WeaponRepository {
    suspend fun getAllWeapons(): List<Weapon>
}