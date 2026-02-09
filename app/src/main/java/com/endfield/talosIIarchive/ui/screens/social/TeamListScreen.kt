package com.endfield.talosIIarchive.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.endfield.talosIIarchive.ui.theme.*
import com.endfield.talosIIarchive.ui.viewmodel.TeamViewModel

@Composable
fun TeamListScreen(
    teamViewModel: TeamViewModel,
    onNewTeamClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        teamViewModel.fetchTeams()
    }

    Box(modifier = Modifier.fillMaxSize().background(TechBackground)) {
        when {
            teamViewModel.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = EndfieldPurple)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "LOADING_TEAM_FILES...",
                        color = EndfieldPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            teamViewModel.errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "SYSTEM_ERROR",
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        teamViewModel.errorMessage ?: "UNKNOWN_ERROR",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            teamViewModel.teams.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "NO_TEAMS_FOUND",
                            color = EndfieldPurple,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "CREATE_YOUR_FIRST_TEAM",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(teamViewModel.teams) { team ->
                        TeamCard(
                            team = team,
                            onClick = {
                                teamViewModel.fetchTeamDetails(team.id)
                            }
                        )
                    }
                }
            }
        }

        // FAB con estilo del tema
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EndfieldPurple)
                    .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                    .clickable { onNewTeamClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Create Team",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun TeamCard(
    team: com.endfield.talosIIarchive.domain.models.Team,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TechBorder)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = TechSurface
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header con ID del equipo
            Text(
                text = "TEAM_${team.id.toString().padStart(3, '0')}",
                color = EndfieldPurple,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre del equipo
            Text(
                text = team.teamName.uppercase(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Descripción
            Text(
                text = team.description,
                color = Color.LightGray,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sección de operadores
            Column {
                Text(
                    "OPERATOR_COMPOSITION",
                    color = EndfieldPurple,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (team.operatorNames.size >= 4) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .border(1.dp, TechBorder)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OperatorNameText(
                                operatorName = team.operatorNames[0],
                                modifier = Modifier.weight(1f)
                            )

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(20.dp)
                                    .background(TechBorder)
                            )

                            OperatorNameText(
                                operatorName = team.operatorNames[1],
                                modifier = Modifier.weight(1f)
                            )

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(20.dp)
                                    .background(TechBorder)
                            )

                            OperatorNameText(
                                operatorName = team.operatorNames[2],
                                modifier = Modifier.weight(1f)
                            )

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(20.dp)
                                    .background(TechBorder)
                            )

                            OperatorNameText(
                                operatorName = team.operatorNames[3],
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Estado del equipo
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .align(Alignment.End)
                    ) {
                        Text(
                            "COMPLETE",
                            color = EndfieldPurple,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .border(1.dp, Color.Red.copy(alpha = 0.5f))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TEAM_INCOMPLETE (${team.operatorNames.size}/4)",
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OperatorNameText(
    operatorName: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = operatorName,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}