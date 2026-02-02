package com.endfield.talosIIarchive.domain.models

import kotlinx.serialization.Serializable

@Serializable // Lo necesitarás para Ktor
data class Weapon(
    val id: Long,
    val name: String,
    val type: String,
    val rarity: Int
)

@Serializable
data class Gear(
    val id: Long,
    val name: String,
    val type: GearType,
    val setId: Long,
    val setName: String
)