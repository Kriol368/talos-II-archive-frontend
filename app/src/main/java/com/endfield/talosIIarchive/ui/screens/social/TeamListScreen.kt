package com.endfield.talosIIarchive.ui.screens.social

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.endfield.talosIIarchive.ui.theme.EndfieldPurple
import com.endfield.talosIIarchive.ui.theme.TechSurface
import com.endfield.talosIIarchive.ui.viewmodel.TeamViewModel

@Composable
fun TeamListScreen(teamViewModel: TeamViewModel) {
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        teamViewModel.fetchTeams()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            teamViewModel.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = EndfieldPurple)
                    Spacer(Modifier.height(16.dp))
                    Text("LOADING_TEAMS...", color = Color.Gray)
                }
            }

            teamViewModel.errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("ERROR", color = Color.Red, fontWeight = FontWeight.Bold)
                    Text(teamViewModel.errorMessage ?: "Unknown error", color = Color.Gray)
                }
            }

            teamViewModel.teams.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("NO_TEAMS_FOUND", color = Color.Gray)
                    Text(
                        "Create your first team!",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            else -> {
                LazyColumn(
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

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = EndfieldPurple,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Team")
        }
    }

    if (showCreateDialog) {
        Text("New Team Screen Coming Soon")
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
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = TechSurface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = team.teamName,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = team.description,
                color = Color.Gray,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (team.operatorNames.size >= 4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OperatorNameText(
                        operatorName = team.operatorNames[0],
                        modifier = Modifier.weight(1f)
                    )

                    OperatorNameText(
                        operatorName = team.operatorNames[1],
                        modifier = Modifier.weight(1f)
                    )

                    OperatorNameText(
                        operatorName = team.operatorNames[2],
                        modifier = Modifier.weight(1f)
                    )

                    OperatorNameText(
                        operatorName = team.operatorNames[3],
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Text(
                    text = "Team incomplete (${team.operatorNames.size}/4 operators)",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun OperatorNameText(
    operatorName: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = operatorName,
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        modifier = modifier,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}