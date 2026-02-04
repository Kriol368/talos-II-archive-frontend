package com.endfield.talosIIarchive.ui.screens.wiki

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.endfield.talosIIarchive.domain.models.Operator
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldYellow
import com.endfield.talosIIarchive.ui.theme.TechBlack

//Aca faltaria mejorar el formato de las descripciones y tal (justificar el texto) y añadir el tipo de arma que usa, elemento y rareza

@Composable
fun OperatorDetailScreen(operator: Operator, onBack: () -> Unit) {
    var activeTab by remember { mutableStateOf("SKILLS") }
    val scrollState = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize(), color = TechBlack) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {

                Box(
                    modifier = Modifier
                        .height(380.dp)
                        .fillMaxWidth()
                ) {
                    AsyncImage(
                        model = "http://10.0.2.2:8080${operator.imageUrl}",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        TechBlack
                                    )
                                )
                            )
                    )

                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .statusBarsPadding()
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                    }

                    Column(
                        Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Text(
                            "REC // PERSONAL_FILE",
                            color = EndfieldYellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            operator.name.uppercase(),
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        DataTag("CLASS", operator.operatorClass, EndfieldYellow, Color.Black)
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FunctionalStatItem("STR", operator.strength)
                    FunctionalStatItem("AGI", operator.agility)
                    FunctionalStatItem("INT", operator.intellect)
                    FunctionalStatItem("WILL", operator.will)
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EndfieldTabButton(
                        "Skills",
                        "MOD_01",
                        activeTab == "SKILLS",
                        Modifier.weight(1f)
                    ) {
                        activeTab = "SKILLS"
                    }
                    EndfieldTabButton(
                        "Talents",
                        "MOD_02",
                        activeTab == "TALENTS",
                        Modifier.weight(1f)
                    ) {
                        activeTab = "TALENTS"
                    }
                    EndfieldTabButton(
                        "Potential",
                        "MOD_03",
                        activeTab == "POTENTIAL",
                        Modifier.weight(1f)
                    ) {
                        activeTab = "POTENTIAL"
                    }
                }

                Box(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                        .heightIn(min = 400.dp)
                ) {
                    when (activeTab) {
                        "SKILLS" -> Column {
                            SkillBlock(
                                operator.basicAttack,
                                "BASIC_ATK",
                                operator.basicAttackDescription
                            )
                            SkillBlock(
                                operator.battleSkill,
                                "BATTLE_SKILL",
                                operator.battleSkillDescription
                            )
                            SkillBlock(
                                operator.comboSkill,
                                "COMBO_SKILL",
                                operator.comboSkillDescription
                            )
                            SkillBlock(
                                operator.ultimate,
                                "ULTIMATE",
                                operator.ultimateDescription
                            )
                        }

                        "TALENTS" -> Column {
                            TalentBlock("COMBAT_TALENT_01", operator.combatTalent1)
                            TalentBlock("COMBAT_TALENT_02", operator.combatTalent2)
                            TalentBlock("BASE_LOGISTICS_01", operator.baseTalent1)
                            TalentBlock("BASE_LOGISTICS_02", operator.baseTalent2)

                        }

                        "POTENTIAL" -> Column {
                            PotentialRow("P1", operator.p1, operator.p1Effect)
                            PotentialRow("P2", operator.p2, operator.p2Effect)
                            PotentialRow("P3", operator.p3, operator.p3Effect)
                            PotentialRow("P4", operator.p4, operator.p4Effect)
                            PotentialRow("P5", operator.p5, operator.p5Effect)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }


        }
    }
}

@Composable
fun SkillBlock(name: String?, type: String, desc: String?) {
    if (name == null) return
    Column(Modifier.padding(bottom = 20.dp)) {
        Text(type, color = EndfieldCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(name.uppercase(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
        Text(desc ?: "", color = Color.LightGray, fontSize = 12.sp, lineHeight = 16.sp)
    }
}