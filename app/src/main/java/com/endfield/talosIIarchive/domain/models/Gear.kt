package com.endfield.talosIIarchive.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Gear(
    val id: Long,
    val name: String,
    val type: GearType,
    val setId: Long,
    val setName: String
)