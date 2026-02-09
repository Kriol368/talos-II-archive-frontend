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
import com.endfield.talosIIarchive.domain.models.Gear
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldOrange
import com.endfield.talosIIarchive.ui.theme.EndfieldYellow
import com.endfield.talosIIarchive.ui.theme.TechBlack
import com.endfield.talosIIarchive.ui.theme.TechSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GearDetailScreen(gear: Gear,isLoadingFullData: Boolean, onBack: () -> Unit) {
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
                        model = gear.imageUrl,
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
                            "REC // GEAR_FILE",
                            color = EndfieldYellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            gear.name.uppercase(),
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        DataTag("TYPE", gear.gearType, EndfieldCyan, Color.Black)
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .background(
                                EndfieldOrange, shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            gear.gearSet,
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    GearStatItem("DEF", gear.def.toString())
                    GearStatItem("TYPE", gear.gearType)
                    GearStatItem("SET", gear.gearSet.take(6))
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EndfieldTabButton(
                        "Attributes", "MOD_01", activeTab == "STATS", Modifier.weight(1f)
                    ) {
                        activeTab = "STATS"
                    }
                    EndfieldTabButton(
                        "Set Bonus", "MOD_02", activeTab == "BONUS", Modifier.weight(1f)
                    ) {
                        activeTab = "BONUS"
                    }
                }

                Box(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    when (activeTab) {
                        "STATS" -> GearStatContent(gear)
                        "BONUS" -> SetBonusContent(gear)
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun GearStatItem(label: String, value: String) {
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
fun GearStatContent(gear: Gear) {
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

                GearStatRow("DEF", gear.def.toString())

                gear.attribute1?.let { attr ->
                    if (attr.isNotBlank()) {
                        GearStatRow("Attribute 1", attr)
                    }
                }

                gear.attribute2?.let { attr ->
                    if (attr.isNotBlank()) {
                        GearStatRow("Attribute 2", attr)
                    }
                }

                gear.attribute3?.let { attr ->
                    if (attr.isNotBlank()) {
                        GearStatRow("Attribute 3", attr)
                    }
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
                    "GEAR INFORMATION",
                    color = EndfieldCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                GearInfoRow("Gear Type", gear.gearType)
                GearInfoRow("Gear Set", gear.gearSet)
            }
        }
    }
}

@Composable
fun SetBonusContent(gear: Gear) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TechSurface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "SET BONUS (3-PIECE)",
                color = EndfieldOrange,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (!gear.setBonus.isNullOrBlank()) {
                Text(
                    gear.setBonus, color = Color.White, fontSize = 14.sp, lineHeight = 20.sp
                )
            } else {
                Text(
                    "No set bonus information available",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun GearStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label, color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold
        )
        Text(
            value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 2
        )
    }
}

@Composable
fun GearInfoRow(label: String, value: String) {
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