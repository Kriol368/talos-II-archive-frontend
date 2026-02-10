package com.endfield.talosIIarchive.domain.models

import kotlinx.serialization.Serializable


@Serializable
data class Operator(
    val id: Int,
    val name: String,
    val rarity: String,
    val imageUrl: String,
    val element: String,
    val weaponType: String,
    val operatorClass: String,
    val strength: Int = 0,
    val agility: Int = 0,
    val intellect: Int = 0,
    val will: Int = 0,
    val basicAttack: String? = null,
    val basicAttackDescription: String? = null,
    val battleSkill: String? = null,
    val battleSkillDescription: String? = null,
    val battleSkillSpCost: Int = 0,
    val comboSkill: String? = null,
    val comboSkillDescription: String? = null,
    val comboSkillCooldown: Int = 0,
    val ultimate: String? = null,
    val ultimateDescription: String? = null,
    val ultimateEnergyCost: Int = 0,
    val baseTalent1: String? = null,
    val baseTalent2: String? = null,
    val combatTalent1: String? = null,
    val combatTalent2: String? = null,
    val p1: String? = null, val p1Effect: String? = null,
    val p2: String? = null, val p2Effect: String? = null,
    val p3: String? = null, val p3Effect: String? = null,
    val p4: String? = null, val p4Effect: String? = null,
    val p5: String? = null, val p5Effect: String? = null
)