package com.endfield.talosIIarchive.ui.screens.social

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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.endfield.talosIIarchive.ui.theme.*
import com.endfield.talosIIarchive.ui.viewmodel.BlueprintViewModel
import com.endfield.talosIIarchive.ui.viewmodel.TeamViewModel

enum class SocialCategory {
    TEAMS, BLUEPRINTS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(
    blueprintViewModel: BlueprintViewModel,
    teamViewModel: TeamViewModel,
    newTeamViewModel: NewTeamViewModel
) {
    // CAMBIO CLAVE: rememberSaveable para no volver atrás al girar
    var selectedCategory by rememberSaveable { mutableStateOf<SocialCategory?>(null) }
    var showNewTeamScreen by rememberSaveable { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val infiniteTransition = rememberInfiniteTransition(label = "SocialNeonWave")
    val pulsePosition by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "SocialWaveProgress"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = TechBackground) {
        when {
            // 1. Pantalla de creación de equipo
            showNewTeamScreen -> {
                NewTeamScreen(
                    teamViewModel = teamViewModel,
                    newTeamViewModel = newTeamViewModel,
                    onBack = { showNewTeamScreen = false },
                    onCreateSuccess = {
                        showNewTeamScreen = false
                        selectedCategory = SocialCategory.TEAMS
                        teamViewModel.fetchTeams()
                    }
                )
            }

            // 2. Detalle de equipo (Overlay)
            teamViewModel.selectedTeam != null -> {
                TeamDetailScreen(teamViewModel.selectedTeam!!) {
                    teamViewModel.clearSelectedTeam()
                }
            }

            // 3. Cargando detalle
            teamViewModel.isDetailLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = EndfieldPurple)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "LOADING_TEAM_FILE...",
                            color = EndfieldPurple,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            // 4. Categorías o Menú Principal
            else -> {
                if (selectedCategory != null) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "// ${selectedCategory.toString()}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    letterSpacing = 1.sp
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    selectedCategory = null
                                    teamViewModel.clearSelectedTeam()
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
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
                            SocialCategory.TEAMS -> {
                                TeamListScreen(
                                    teamViewModel = teamViewModel,
                                    onNewTeamClick = { showNewTeamScreen = true }
                                )
                            }
                            SocialCategory.BLUEPRINTS -> {
                                BlueprintScreen(blueprintViewModel)
                            }
                            else -> {}
                        }
                    }
                } else {
                    // --- MENÚ PRINCIPAL ADAPTATIVO ---
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "// COMMUNITY_HUB_V.1.0",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (isLandscape) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp), // Altura fija para botones en horizontal
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SocialMenuButton(
                                    number = "04",
                                    title = "TEAMS",
                                    accentColor = EndfieldPurple,
                                    buttonIndex = 0f,
                                    currentWave = pulsePosition,
                                    modifier = Modifier.weight(1f),
                                    isLandscape = true
                                ) {
                                    selectedCategory = SocialCategory.TEAMS
                                }

                                SocialMenuButton(
                                    number = "05",
                                    title = "BLUEPRINTS",
                                    accentColor = EndfieldGreen,
                                    buttonIndex = 1f,
                                    currentWave = pulsePosition,
                                    modifier = Modifier.weight(1f),
                                    isLandscape = true
                                ) {
                                    selectedCategory = SocialCategory.BLUEPRINTS
                                }
                            }
                        } else {

                                SocialMenuButton(
                                    number = "04",
                                    title = "TEAMS",
                                    accentColor = EndfieldPurple,
                                    buttonIndex = 0f,
                                    currentWave = pulsePosition,
                                    modifier = Modifier.fillMaxWidth().height(160.dp),
                                    isLandscape = false
                                ) {
                                    selectedCategory = SocialCategory.TEAMS
                                }
                            Spacer(modifier = Modifier.height(12.dp))
                                SocialMenuButton(
                                    number = "05",
                                    title = "BLUEPRINTS",
                                    accentColor = EndfieldGreen,
                                    buttonIndex = 1f,
                                    currentWave = pulsePosition,
                                    modifier = Modifier.fillMaxWidth().height(160.dp),
                                    isLandscape = false
                                ) {
                                    selectedCategory = SocialCategory.BLUEPRINTS
                                }
                            }
                        }
                    }
                }
            }
        }
    }


@Composable
fun SocialMenuButton(
    number: String,
    title: String,
    accentColor: Color,
    buttonIndex: Float,
    currentWave: Float,
    modifier: Modifier,
    isLandscape: Boolean,
    onClick: () -> Unit
) {
    val distance = Math.abs(currentWave - buttonIndex)
    val intensity = (1f - distance).coerceIn(0f, 1f)
    val textAlpha = (0.7f + (intensity * 0.3f)).coerceIn(0.7f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(TechSurface)
            .border(if (isLandscape) 1.dp else 0.dp, TechBorder)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(6.dp)
                .graphicsLayer { alpha = (0.2f + intensity * 0.8f) }
                .background(accentColor)
        )

        Column(
            modifier = Modifier
                .padding(if (isLandscape) 16.dp else 24.dp)
                .align(Alignment.CenterStart)
        ) {
            Text(
                text = "ID.$number",
                color = accentColor.copy(alpha = (0.4f + intensity * 0.6f)),
                fontSize = if (isLandscape) 10.sp else 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Text(
                text = title,
                color = Color.White,
                fontSize = if (isLandscape) 24.sp else 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = if (isLandscape) 2.sp else 4.sp,
                modifier = Modifier.graphicsLayer { alpha = textAlpha }
            )
        }

        if (!isLandscape) {
            Text(
                text = "CONNECT >",
                color = Color.White.copy(alpha = 0.2f + (intensity * 0.4f)),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}