package com.endfield.talosIIarchive.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Gear(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val gearType: String,
    val gearSet: String,
    val def: Int,
    val attribute1: String? = null,
    val attribute2: String? = null,
    val attribute3: String? = null,
    val setBonus: String? = null
)