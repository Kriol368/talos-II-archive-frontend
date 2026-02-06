package com.endfield.talosIIarchive.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: Int,
    val teamName: String,
    val description: String,
    val createdAt: String,
    val operatorNames: List<String> = emptyList()
)

@Serializable
data class TeamDetail(
    val id: Int,
    val teamName: String,
    val description: String,
    val createdAt: String,
    val operators: List<TeamOperatorDetail>,
    val totalSetBonuses: Map<String, List<String>>
)


@Serializable
data class TeamOperatorDetail(
    val operatorPosition: Int,
    val operatorName: String,
    val operatorImage: String,
    val weaponName: String,
    val weaponImage: String,
    val armorGear: Gear? = null,
    val glovesGear: Gear? = null,
    val kit1Gear: Gear? = null,
    val kit2Gear: Gear? = null,
    val activeSetBonuses: List<String> = emptyList()
)

@Serializable
data class CreateTeamRequest(
    val teamName: String,
    val description: String,
    val operatorIds: List<Int>,
    val weaponIds: List<Int>,
    val gearIds: List<List<Int>>
)