package com.endfield.talosIIarchive.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Operator(
    val id: Int,
    val name: String,
    val rarity: String,      // Ahora es un String como "6★"
    val imageUrl: String,    // El backend ya usa camelCase
    val element: String,     // Viene el nombre, no el ID
    val weaponType: String,
    val operatorClass: String
)
data class TeamOperator(
    val operator: Operator,
    val weapon: Weapon? = null,
    val armor: Gear? = null,
    val gloves: Gear? = null,
    val kit1: Gear? = null,
    val kit2: Gear? = null
) {
    fun getActiveSetBonuses(): List<String> {
        val equippedGear = listOfNotNull(armor, gloves, kit1, kit2)
        return equippedGear
            .groupBy { it.setId }
            .filter { it.value.size >= 3 }
            .map { it.value.first().setName }
    }
}