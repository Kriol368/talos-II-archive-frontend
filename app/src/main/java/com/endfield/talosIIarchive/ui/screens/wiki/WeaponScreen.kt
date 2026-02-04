package com.endfield.talosIIarchive.ui.screens.wiki

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.endfield.talosIIarchive.domain.models.Weapon

@Composable
fun WeaponListScreen(){

}

/*
@Composable
fun WeaponListScreen(viewModel: OperatorViewModel) {
    LaunchedEffect(Unit) {
        viewModel.fetchWeapons()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (viewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.Cyan
            )
        } else if (viewModel.weapons.isEmpty()) {
            // 👈 AÑADE ESTO: Si no hay armas, mostramos este texto
            Text(
                "No se encontraron armas en la DB",
                modifier = Modifier.align(Alignment.Center),
                color = Color.Gray
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.weapons) { weapon ->
                    WeaponCard(weapon)
                }
            }
        }
    }
}

 */
@Composable
fun WeaponCard(weapon: Weapon) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFF1E1E1E) // Gris oscuro
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // IMAGEN (Usamos la URL directamente porque el backend ya la manda completa)
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
                // Estrellas doradas
                Text(
                    text = weapon.rarity,
                    color = Color(0xFFFFD700),
                    style = MaterialTheme.typography.bodyMedium
                )

                // Solo mostramos la pasiva si el servidor la envía
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