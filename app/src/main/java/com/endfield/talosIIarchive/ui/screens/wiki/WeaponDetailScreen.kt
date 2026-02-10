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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
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
    var activeTab by remember { mutableStateOf("STATS") }
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = { onBack() },
        sheetState = sheetState,
        containerColor = TechBlack,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        dragHandle = {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Close",
                tint = EndfieldYellow.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(30.dp)
                    .clickable { onBack() }
            )
        },
        shape = androidx.compose.ui.graphics.RectangleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TechBlack)
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .height(380.dp)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = if (weapon.imageUrl.startsWith("http")) weapon.imageUrl
                    else "http://158.179.216.16:8080${weapon.imageUrl}",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, TechBlack)))
                )

                Column(
                    modifier = Modifier
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
                        lineHeight = 40.sp
                    )
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        DataTag("TYPE", weapon.weaponType, EndfieldCyan, Color.Black)
                        DataTag(
                            "RARITY",
                            weapon.rarity,
                            if (weapon.rarity.contains("6")) EndfieldOrange else EndfieldCyan,
                            Color.Black
                        )
                        if (!weapon.passive.isNullOrBlank()) {
                            DataTag("PASSIVE", "✓", EndfieldYellow, Color.Black)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isLoadingFullData) {
                    repeat(3) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = EndfieldCyan
                        )
                    }
                } else {
                    WeaponStatItem("ATK", weapon.baseAtk.toString())
                    WeaponStatItem("RARITY", weapon.rarity)
                    WeaponStatItem("TYPE", weapon.weaponType)
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EndfieldTabButton(
                    "Stats",
                    "MOD_01",
                    activeTab == "STATS",
                    Modifier.weight(1f)
                ) { activeTab = "STATS" }
                EndfieldTabButton(
                    "Passive",
                    "MOD_02",
                    activeTab == "PASSIVE",
                    Modifier.weight(1f)
                ) { activeTab = "PASSIVE" }
            }

            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .heightIn(min = 300.dp)
            ) {
                if (isLoadingFullData) {
                    Text(
                        "SYNCHRONIZING_WEAPON_DATA...",
                        color = EndfieldCyan,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    when (activeTab) {
                        "STATS" -> StatContent(weapon)
                        "PASSIVE" -> PassiveContent(weapon)
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

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

                WeaponStatRow("Base ATK", weapon.baseAtk.toString())

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "ADDITIONAL STATS",
                    color = EndfieldCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                weapon.stat1?.let { stat ->
                    if (stat.isNotBlank()) {
                        WeaponAttributeRow("STAT_01", stat)
                    }
                }

                weapon.stat2?.let { stat ->
                    if (stat.isNotBlank()) {
                        WeaponAttributeRow("STAT_02", stat)
                    }
                }

                if (weapon.stat1.isNullOrBlank() && weapon.stat2.isNullOrBlank()) {
                    Text(
                        "No additional stats",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

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

                WeaponInfoRow("Weapon Type", weapon.weaponType)
                WeaponInfoRow("Rarity", weapon.rarity)
                WeaponInfoRow("ID", weapon.id.toString())
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
                val formattedPassive = weapon.passive
                    .replace("\r\n", "\n")
                    .replace("\r", "\n")

                Text(
                    formattedPassive,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "PASSIVE_DATA_UNAVAILABLE",
                        color = EndfieldYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Complete weapon details not loaded",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Text(
                        "Weapon: ${weapon.name}",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WeaponStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WeaponAttributeRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(EndfieldCyan.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                label,
                color = EndfieldCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun WeaponInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            value,
            color = EndfieldCyan,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}