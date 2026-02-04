package com.endfield.talosIIarchive.domain.repositoty

import com.endfield.talosIIarchive.domain.models.Operator
import com.endfield.talosIIarchive.domain.models.Weapon


interface OperatorRepository {
    suspend fun getAllOperators(): List<Operator>
    suspend fun getOperatorById(id: Int): Operator?

    // --- AÑADE ESTO ---
    suspend fun getAllWeapons(): List<Weapon>
}