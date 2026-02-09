package com.endfield.talosIIarchive.ui.screens.wiki

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.endfield.talosIIarchive.domain.models.Weapon
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldOrange
import com.endfield.talosIIarchive.ui.theme.EndfieldYellow
import com.endfield.talosIIarchive.ui.theme.TechBlack
import com.endfield.talosIIarchive.ui.theme.TechSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeaponDetailScreen(weapon: Weapon, isLoadingFullData: Boolean, onBack: () -> Unit) {


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onBack() },
        sheetState = sheetState,
        containerColor = TechBlack,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        dragHandle = {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Cerrar",
                tint = EndfieldYellow.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 12.dp).size(30.dp).clickable { onBack() }
            )
        },
        shape = androidx.compose.ui.graphics.RectangleShape
    ) {
        var activeTab by remember { mutableStateOf("STATS") }
        val scrollState = rememberScrollState()






            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Header with image
                Box(
                    modifier = Modifier
                        .height(380.dp)
                        .fillMaxWidth()
                ) {
                    AsyncImage(
                        model = weapon.imageUrl,
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
                                        Color.Transparent, TechBlack
                                    )
                                )
                            )
                    )

                    IconButton(
                        onClick = onBack, modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                    }

                    Column(
                        Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Text(
                            "REC // WEAPON_FILE",
                            color = EndfieldYellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                            Text(
                                weapon.name.uppercase(),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                lineHeight = 40.sp,
                                )
                        DataTag("TYPE", weapon.weaponType, EndfieldCyan, Color.Black)
                    }

                    // Rarity badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .background(
                                if (weapon.rarity.contains("6")) EndfieldOrange else EndfieldCyan,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            weapon.rarity,
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Stats row - Fixed: Create a custom version for weapon
                Row(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeaponStatItem("ATK", weapon.baseAtk.toString())
                    WeaponStatItem("RARITY", weapon.rarity)
                    WeaponStatItem("TYPE", weapon.weaponType)
                }

                // Tabs
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EndfieldTabButton(
                        "Stats", "MOD_01", activeTab == "STATS", Modifier.weight(1f)
                    ) {
                        activeTab = "STATS"
                    }
                    EndfieldTabButton(
                        "Passive", "MOD_02", activeTab == "PASSIVE", Modifier.weight(1f)
                    ) {
                        activeTab = "PASSIVE"
                    }
                }

                // Content
                Box(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    when (activeTab) {
                        "STATS" -> StatContent(weapon)
                        "PASSIVE" -> PassiveContent(weapon)
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }


// Custom stat item for weapons that accepts String values
@Composable
fun WeaponStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Box(
            Modifier
                .size(15.dp, 2.dp)
                .background(EndfieldCyan)
        )
    }
}

@Composable
fun StatContent(weapon: Weapon) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Base Stats Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TechSurface),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "BASE STATISTICS",
                    color = EndfieldCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                StatRow("Base ATK", weapon.baseAtk.toString())

                weapon.stat1?.let { stat ->
                    StatRow("Primary Stat", stat)
                }

                weapon.stat2?.let { stat ->
                    StatRow("Secondary Stat", stat)
                }
            }
        }

        // Weapon Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TechSurface),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "WEAPON INFORMATION",
                    color = EndfieldCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                InfoRow("Weapon Type", weapon.weaponType)
                InfoRow("Rarity", weapon.rarity)
            }
        }
    }
}

@Composable
fun PassiveContent(weapon: Weapon) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TechSurface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "WEAPON PASSIVE",
                color = EndfieldOrange,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (!weapon.passive.isNullOrBlank()) {
                Text(
                    weapon.passive, color = Color.White, fontSize = 14.sp, lineHeight = 20.sp
                )
            } else {
                Text(
                    "No passive ability",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label, color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold
        )
        Text(
            value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label, color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold
        )
        Text(
            value, color = EndfieldCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold
        )
    }
}