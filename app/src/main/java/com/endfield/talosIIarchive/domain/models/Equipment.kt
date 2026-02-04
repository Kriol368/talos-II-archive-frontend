package com.endfield.talosIIarchive.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Weapon(
    val id: Int,
    val name: String,
    val rarity: String,      // Recibe "6★"
    val imageUrl: String,    // Recibe la URL completa de wiki.gg
    val weaponType: String,  // Recibe "Sword", "Handcannon", etc.
    val baseAtk: Int,        // Recibe 510, 490, etc.
    // Hacemos estos opcionales con un valor por defecto por si el JSON no los trae
    val passive: String? = null,
    val stat1: String? = null,
    val stat2: String? = null
)

@Serializable
data class Blueprint(
    val id: Int,
    val title: String,
    val description: String?,
    val codeHash: String,
    val createdAt: String
)

@Serializable
data class Gear(
    val id: Long,
    val name: String,
    val type: GearType,
    val setId: Long,
    val setName: String
)