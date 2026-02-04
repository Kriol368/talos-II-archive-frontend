package com.endfield.talosIIarchive.ui.screens.wiki

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.endfield.talosIIarchive.domain.models.Weapon
import com.endfield.talosIIarchive.ui.viewmodel.WeaponViewModel


@Composable
fun WeaponListScreen(
    weaponViewModel: WeaponViewModel
) {
    LaunchedEffect(Unit) {
        weaponViewModel.fetchWeapons()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            weaponViewModel.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Color.Cyan)
                    Spacer(Modifier.height(16.dp))
                    Text("LOADING_WEAPON_DATA...", color = Color.Gray)
                }
            }

            weaponViewModel.errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("ERROR", color = Color.Red, fontWeight = FontWeight.Bold)
                    Text(weaponViewModel.errorMessage ?: "Unknown error", color = Color.Gray)
                }
            }

            weaponViewModel.weapons.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("NO_WEAPONS_FOUND", color = Color.Gray)
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(weaponViewModel.weapons) { weapon ->
                        WeaponCard(weapon)
                    }
                }
            }
        }
    }
}

@Composable
fun WeaponCard(weapon: Weapon) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = weapon.imageUrl,
                contentDescription = weapon.name,
                modifier = Modifier
                    .size(70.dp)
                    .background(Color(0xFF2D2D2D), RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = weapon.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "${weapon.weaponType} | ATK: ${weapon.baseAtk}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Cyan
                )
                Text(
                    text = weapon.rarity,
                    color = Color(0xFFFFD700),
                    style = MaterialTheme.typography.bodyMedium
                )

                if (!weapon.passive.isNullOrBlank()) {
                    Text(
                        text = weapon.passive,
                        color = Color.LightGray,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2
                    )
                }
            }
        }
    }
}