package com.endfield.talosIIarchive.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Operator(
    val id: Int,
    val name: String,
    val rarity: String,
    val imageUrl: String,
    val operatorClass: String,
    val element: String,
    // Stats: con valor por defecto 0 por si no llegan
    val strength: Int = 0,
    val agility: Int = 0,
    val intellect: Int = 0,
    val will: Int = 0,
    // Habilidades: con valor nulo por si el JSON no las trae aún
    val basic_attack: String? = null,
    val basic_attack_desc: String? = null,
    val battle_skill: String? = null,
    val battle_skill_desc: String? = null,
    val ultimate: String? = null,
    val ultimate_desc: String? = null
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