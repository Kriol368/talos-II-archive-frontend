package com.endfield.talosIIarchive.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun SocialScreen(blueprintViewModel: BlueprintViewModel, teamViewModel: TeamViewModel, newTeamViewModel: NewTeamViewModel) {
    var selectedCategory by remember { mutableStateOf<SocialCategory?>(null) }
    var showNewTeamScreen by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = TechBackground) {
        when {
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

            // SECOND: Check if we're showing a team detail
            teamViewModel.selectedTeam != null -> {
                TeamDetailScreen(teamViewModel.selectedTeam!!) {
                    teamViewModel.clearSelectedTeam()
                }
            }

            // THIRD: Check if team detail is loading
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

            // FOURTH: Show the main screen based on selected category
            else -> {
                when (selectedCategory) {
                    SocialCategory.TEAMS -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "// TEAMS",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        letterSpacing = 1.sp
                                    )
                                }, navigationIcon = {
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
                                }, colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = TechSurface, titleContentColor = Color.White
                                )
                            )
                            TeamListScreen(
                                teamViewModel = teamViewModel,
                                onNewTeamClick = { showNewTeamScreen = true }  // Pass callback
                            )
                        }
                    }

                    SocialCategory.BLUEPRINTS -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "// BLUEPRINTS",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        letterSpacing = 1.sp
                                    )
                                }, navigationIcon = {
                                    IconButton(onClick = { selectedCategory = null }) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null,
                                            tint = EndfieldOrange
                                        )
                                    }
                                }, colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = TechSurface, titleContentColor = Color.White
                                )
                            )
                            BlueprintScreen(blueprintViewModel)
                        }
                    }

                    null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "// COMMUNITY_HUB_V.1.0",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            SocialMenuButton("04", "TEAMS", EndfieldPurple, Modifier.weight(1f)) {
                                selectedCategory = SocialCategory.TEAMS
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            SocialMenuButton("05", "BLUEPRINTS", EndfieldGreen, Modifier.weight(1f)) {
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
    number: String, title: String, accentColor: Color, modifier: Modifier, onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, TechBorder)
            .background(TechSurface)
            .clickable { onClick() }) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(6.dp)
                .background(accentColor)
        )

        Column(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.CenterStart)
        ) {
            Text(
                text = "ID.$number",
                color = accentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )
        }

        Text(
            text = "ACCESS >",
            color = Color.DarkGray,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            fontSize = 10.sp
        )
    }
}