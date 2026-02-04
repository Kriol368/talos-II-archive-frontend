package com.endfield.talosIIarchive.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Weapon(
    val id: Int,
    val name: String,
    val rarity: String,
    val imageUrl: String,
    val weaponType: String,
    val baseAtk: Int,
    val passive: String? = null,
    val stat1: String? = null,
    val stat2: String? = null
)



