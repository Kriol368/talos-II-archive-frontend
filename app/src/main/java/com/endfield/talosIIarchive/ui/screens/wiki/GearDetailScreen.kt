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
import com.endfield.talosIIarchive.domain.models.Gear
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldOrange
import com.endfield.talosIIarchive.ui.theme.EndfieldYellow
import com.endfield.talosIIarchive.ui.theme.TechBlack
import com.endfield.talosIIarchive.ui.theme.TechSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GearDetailScreen(gear: Gear, isLoadingFullData: Boolean, onBack: () -> Unit) {
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
                contentDescription = "Cerrar",
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
            // --- 1. HEADER ---
            Box(
                modifier = Modifier
                    .height(380.dp)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = if (gear.imageUrl.startsWith("http")) gear.imageUrl
                    else "http://158.179.216.16:8080${gear.imageUrl}",
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
                        "REC // GEAR_FILE",
                        color = EndfieldYellow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        gear.name.uppercase(),
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        lineHeight = 44.sp
                    )
                    // Etiquetas técnicas
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        DataTag("TYPE", gear.gearType, EndfieldCyan, Color.Black)
                        DataTag("SET", gear.gearSet.take(8), EndfieldOrange, Color.Black)
                        // Indicador visual de setBonus
                        if (!gear.setBonus.isNullOrBlank()) {
                            DataTag("BONUS", "✓", EndfieldYellow, Color.Black)
                        }
                    }
                }
            }

            // --- 2. STATS PRINCIPALES ---
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
                            color = EndfieldYellow
                        )
                    }
                } else {
                    GearStatItem("DEF", gear.def.toString())
                    GearStatItem("TYPE", gear.gearType)
                    GearStatItem("SET", gear.gearSet.take(6))
                }
            }

            // --- 3. SELECTOR DE MÓDULOS ---
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

            // --- 4. PANEL DE CONTENIDO ---
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .heightIn(min = 300.dp)
            ) {
                if (isLoadingFullData) {
                    Text(
                        "UPLOADING_GEAR_PARAMETERS...",
                        color = EndfieldYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    when (activeTab) {
                        "STATS" -> GearStatContent(gear)
                        "BONUS" -> SetBonusContent(gear)
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
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
        // CARD 1: BASE STATS Y ATRIBUTOS
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

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "ATTRIBUTES",
                    color = EndfieldCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Mostrar los 3 atributos si existen
                gear.attribute1?.let { attr ->
                    if (attr.isNotBlank()) {
                        GearAttributeRow("ATTR_01", attr)
                    }
                }

                gear.attribute2?.let { attr ->
                    if (attr.isNotBlank()) {
                        GearAttributeRow("ATTR_02", attr)
                    }
                }

                gear.attribute3?.let { attr ->
                    if (attr.isNotBlank()) {
                        GearAttributeRow("ATTR_03", attr)
                    }
                }

                // Si no hay atributos, mostrar mensaje
                if (gear.attribute1.isNullOrBlank() &&
                    gear.attribute2.isNullOrBlank() &&
                    gear.attribute3.isNullOrBlank()) {
                    Text(
                        "No additional attributes",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        // CARD 2: INFORMACIÓN DEL GEAR
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
                GearInfoRow("ID", gear.id.toString())
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
                    gear.setBonus,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "SET_BONUS_DATA_UNAVAILABLE",
                        color = EndfieldYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Complete gear details not loaded",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Text(
                        "Gear Set: ${gear.gearSet}",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
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
            label,
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2
        )
    }
}

@Composable
fun GearAttributeRow(label: String, value: String) {
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
fun GearInfoRow(label: String, value: String) {
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