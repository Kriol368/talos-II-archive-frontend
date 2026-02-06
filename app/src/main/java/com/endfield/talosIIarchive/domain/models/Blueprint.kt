package com.endfield.talosIIarchive.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Blueprint(
    val id: Int,
    val title: String,
    val description: String,
    val codeHash: String,
    val createdAt: String
)