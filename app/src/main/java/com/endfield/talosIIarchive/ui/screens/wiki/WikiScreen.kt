package com.endfield.talosIIarchive.ui.screens.wiki

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldOrange
import com.endfield.talosIIarchive.ui.theme.EndfieldYellow
import com.endfield.talosIIarchive.ui.theme.TechBackground
import com.endfield.talosIIarchive.ui.theme.TechBorder
import com.endfield.talosIIarchive.ui.theme.TechSurface
import com.endfield.talosIIarchive.ui.viewmodel.GearViewModel
import com.endfield.talosIIarchive.ui.viewmodel.OperatorViewModel
import com.endfield.talosIIarchive.ui.viewmodel.WeaponViewModel
import kotlin.math.abs

enum class WikiCategory {
    OPERATORS, WEAPONS, GEAR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WikiScreen(
    operatorViewModel: OperatorViewModel,
    weaponViewModel: WeaponViewModel,
    gearViewModel: GearViewModel
) {
    var selectedCategory by rememberSaveable { mutableStateOf<WikiCategory?>(null) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var selectedOperatorId by remember { mutableStateOf<Int?>(null) }
    var selectedWeaponId by remember { mutableStateOf<Int?>(null) }
    var selectedGearId by remember { mutableStateOf<Int?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize(), color = TechBackground) {
            if (selectedCategory != null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(
                        title = {
                            Text(
                                text = "// ${selectedCategory.toString()}",
                                fontWeight = FontWeight.Black
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                selectedCategory = null
                                selectedOperatorId = null
                                selectedWeaponId = null
                                selectedGearId = null
                                operatorViewModel.clearSelectedOperator()
                                weaponViewModel.clearSelectedWeapon()
                                gearViewModel.clearSelectedGear()
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    null,
                                    tint = EndfieldOrange
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = TechSurface,
                            titleContentColor = Color.White
                        )
                    )

                    when (selectedCategory) {
                        WikiCategory.OPERATORS -> {
                            OperatorListScreen(operatorViewModel) { op ->
                                selectedOperatorId = op.id
                                operatorViewModel.fetchOperatorDetails(op.id)
                            }
                        }

                        WikiCategory.WEAPONS -> {
                            WeaponListScreen(weaponViewModel) { wp ->
                                selectedWeaponId = wp.id
                                weaponViewModel.fetchWeaponDetails(wp.id)
                            }
                        }

                        WikiCategory.GEAR -> {
                            GearListScreen(gearViewModel) { gear ->
                                selectedGearId = gear.id
                                gearViewModel.fetchGearDetails(gear.id)
                            }
                        }

                        else -> {}
                    }
                }
            } else {
                WikiMainMenu(isLandscape) { selectedCategory = it }
            }
        }


        selectedOperatorId?.let { opId ->
            val operatorFull = operatorViewModel.selectedOperatorFull
            val isLoading = operatorViewModel.isDetailLoading

            val basicOperator = operatorViewModel.operators.find { it.id == opId }

            val operatorToShow = if (operatorFull != null && operatorFull.id == opId) {
                operatorFull
            } else {
                basicOperator
            }

            if (operatorToShow != null) {
                OperatorDetailScreen(
                    operator = operatorToShow,
                    isLoadingFullData = isLoading && operatorFull?.id != opId,
                    onBack = {
                        selectedOperatorId = null
                        operatorViewModel.clearSelectedOperator()
                    }
                )
            }
        }

        selectedWeaponId?.let { wpId ->
            val weaponFull = weaponViewModel.selectedWeaponFull
            val isLoading = weaponViewModel.isDetailLoading

            val basicWeapon = weaponViewModel.weapons.find { it.id == wpId }

            val weaponToShow = if (weaponFull != null && weaponFull.id == wpId) {
                weaponFull
            } else {
                basicWeapon
            }

            if (weaponToShow != null) {
                WeaponDetailScreen(
                    weapon = weaponToShow,
                    isLoadingFullData = isLoading && weaponFull?.id != wpId,
                    onBack = {
                        selectedWeaponId = null
                        weaponViewModel.clearSelectedWeapon()
                    }
                )
            }
        }

        selectedGearId?.let { gearId ->
            val gearFull = gearViewModel.selectedGearFull
            val isLoading = gearViewModel.isDetailLoading

            val basicGear = gearViewModel.gearList.find { it.id == gearId }

            val gearToShow = if (gearFull != null && gearFull.id == gearId) {
                gearFull
            } else {
                basicGear
            }

            if (gearToShow != null) {
                GearDetailScreen(
                    gear = gearToShow,
                    isLoadingFullData = isLoading && gearFull?.id != gearId,
                    onBack = {
                        selectedGearId = null
                        gearViewModel.clearSelectedGear()
                    }
                )
            }
        }
    }
}

@Composable
fun WikiMainMenu(isLandscape: Boolean, onCategorySelect: (WikiCategory) -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "NeonWave")
    val pulsePosition by infiniteTransition.animateFloat(
        initialValue = -1f, targetValue = 3f,
        animationSpec = infiniteRepeatable(
            tween(5000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "WaveProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "// ARCHIVE_SYSTEM_V.1.0",
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WikiMenuButton(
                    "01", "OPERATORS", EndfieldOrange, 0f, pulsePosition,
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(), true
                ) { onCategorySelect(WikiCategory.OPERATORS) }

                WikiMenuButton(
                    "02", "WEAPONS", EndfieldCyan, 1f, pulsePosition,
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(), true
                ) { onCategorySelect(WikiCategory.WEAPONS) }

                WikiMenuButton(
                    "03", "GEAR", EndfieldYellow, 2f, pulsePosition,
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(), true
                ) { onCategorySelect(WikiCategory.GEAR) }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WikiMenuButton(
                    "01", "OPERATORS", EndfieldOrange, 0f, pulsePosition,
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(), false
                ) { onCategorySelect(WikiCategory.OPERATORS) }

                WikiMenuButton(
                    "02", "WEAPONS", EndfieldCyan, 1f, pulsePosition,
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(), false
                ) { onCategorySelect(WikiCategory.WEAPONS) }

                WikiMenuButton(
                    "03", "GEAR", EndfieldYellow, 2f, pulsePosition,
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(), false
                ) { onCategorySelect(WikiCategory.GEAR) }
            }
        }
    }
}

@Composable
fun WikiMenuButton(
    number: String,
    title: String,
    accentColor: Color,
    buttonIndex: Float,
    currentWave: Float,
    modifier: Modifier,
    isLandscape: Boolean,
    onClick: () -> Unit
) {
    val distance = abs(currentWave - buttonIndex)
    val intensity = (1f - distance).coerceIn(0f, 1f)
    val textAlpha = (0.7f + (intensity * 0.3f)).coerceIn(0.7f, 1f)

    Box(
        modifier = modifier
            .background(TechSurface)
            .border(if (isLandscape) 1.dp else 0.dp, TechBorder)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(6.dp)
                .background(accentColor.copy(alpha = (0.2f + intensity * 0.8f)))
        )

        Column(
            modifier = Modifier
                .padding(if (isLandscape) 12.dp else 24.dp)
                .align(Alignment.CenterStart)
        ) {
            Text(
                text = "ID.$number",
                color = accentColor.copy(alpha = intensity),
                fontSize = if (isLandscape) 10.sp else 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = Color.White,
                fontSize = if (isLandscape) 22.sp else 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = if (isLandscape) 2.sp else 4.sp,
                modifier = Modifier.graphicsLayer { alpha = textAlpha }
            )
        }

        if (!isLandscape) {
            Text(
                text = "ACCESS >",
                color = Color.White.copy(alpha = 0.2f + (intensity * 0.3f)),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}