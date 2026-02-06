package com.endfield.talosIIarchive.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.endfield.talosIIarchive.domain.models.TeamDetail
import com.endfield.talosIIarchive.domain.models.TeamOperatorDetail
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldOrange
import com.endfield.talosIIarchive.ui.theme.EndfieldPurple
import com.endfield.talosIIarchive.ui.theme.EndfieldYellow
import com.endfield.talosIIarchive.ui.theme.TechBackground
import com.endfield.talosIIarchive.ui.theme.TechBorder
import com.endfield.talosIIarchive.ui.theme.TechSurface

@Composable
fun TeamDetailScreen(
    team: TeamDetail,
    onBack: () -> Unit
) {
    var selectedOperatorIndex by remember { mutableIntStateOf(0) }
    val selectedOperator = team.operators.getOrNull(selectedOperatorIndex)
    val scrollState = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize(), color = TechBackground) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(EndfieldPurple.copy(alpha = 0.2f))
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Text(
                        text = "TEAM DETAILS",
                        color = EndfieldPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = team.teamName,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = team.description,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            OperatorTabsRow(
                operators = team.operators,
                selectedIndex = selectedOperatorIndex,
                onOperatorSelected = { index -> selectedOperatorIndex = index }
            )

            Spacer(modifier = Modifier.height(24.dp))

            selectedOperator?.let { operator ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    OperatorInfoCard(operator)

                    GearSection(operator)
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun OperatorTabsRow(
    operators: List<TeamOperatorDetail>,
    selectedIndex: Int,
    onOperatorSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        operators.forEachIndexed { index, operator ->
            OperatorTabItem(
                operator = operator,
                isSelected = selectedIndex == index,
                onClick = { onOperatorSelected(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun OperatorTabItem(
    operator: TeamOperatorDetail,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) EndfieldPurple else TechSurface
    val borderColor = if (isSelected) EndfieldPurple else TechBorder

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2D2D2D))
                    .border(1.dp, TechBorder, RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = "http://158.179.216.16:8080${operator.operatorImage}",
                    contentDescription = operator.operatorName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = operator.operatorName,
                color = if (isSelected) Color.White else Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun OperatorInfoCard(operator: TeamOperatorDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TechSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OPERATOR DETAILS",
                    color = EndfieldPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                if (operator.activeSetBonuses.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        operator.activeSetBonuses.forEach { setBonus ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        EndfieldPurple.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = setBonus,
                                    color = EndfieldPurple,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2D2D2D))
                            .border(2.dp, EndfieldPurple, RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = "http://158.179.216.16:8080${operator.operatorImage}",
                            contentDescription = operator.operatorName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Text(
                        text = operator.operatorName,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2D2D2D))
                            .border(2.dp, EndfieldCyan, RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = operator.weaponImage,
                            contentDescription = operator.weaponName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "EQUIPPED WEAPON",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = operator.weaponName,
                            color = EndfieldCyan,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GearSection(operator: TeamOperatorDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TechSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "EQUIPPED GEAR",
                color = EndfieldPurple,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    operator.armorGear?.let { gear ->
                        GearCardWithImage(
                            gear = gear,
                            gearType = "ARMOR",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    operator.glovesGear?.let { gear ->
                        GearCardWithImage(
                            gear = gear,
                            gearType = "GLOVES",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    operator.kit1Gear?.let { gear ->
                        GearCardWithImage(
                            gear = gear,
                            gearType = "KIT 1",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    operator.kit2Gear?.let { gear ->
                        GearCardWithImage(
                            gear = gear,
                            gearType = "KIT 2",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GearCardWithImage(
    gear: com.endfield.talosIIarchive.domain.models.Gear,
    gearType: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF252525)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        EndfieldYellow.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = gearType,
                    color = EndfieldYellow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1A1A1A))
                    .border(1.dp, TechBorder, RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = gear.imageUrl,
                    contentDescription = gear.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }

            Text(
                text = gear.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .background(
                        EndfieldOrange.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = gear.gearSet,
                    color = EndfieldOrange,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "DEF",
                    color = Color.Gray,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = gear.def.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}