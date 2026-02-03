package com.endfield.talosIIarchive.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.endfield.talosIIarchive.domain.models.Operator
import com.endfield.talosIIarchive.domain.models.Weapon
import com.endfield.talosIIarchive.ui.viewmodel.OperatorViewModel

// --- PANTALLA PRINCIPAL ---
@Composable
fun WikiScreen(viewModel: OperatorViewModel) {
    // Estado para saber qué pestaña está seleccionada (0 = Operadores, 1 = Armas)
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Personajes", "Armas")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212) // Tu fondo negro grisáceo
    ) {
        Column {
            // --- SELECTOR DE PESTAÑAS ---
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFF1C1C1C),
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color.Cyan // Color del subrayado
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // --- CONTENIDO SEGÚN LA PESTAÑA ---
            when (selectedTabIndex) {
                0 -> OperatorList(viewModel) // La lista que ya teníamos
                1 -> WeaponListScreen(viewModel) // Nueva lista de armas
            }
        }
    }
}

// --- LISTA DE OPERADORES ---
@Composable
fun OperatorList(viewModel: OperatorViewModel) {
    LaunchedEffect(Unit) {
        viewModel.fetchOperators()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (viewModel.operators.isEmpty()) {
            Text(
                "No se encontraron operadores",
                modifier = Modifier.align(Alignment.Center),
                color = Color.Gray
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.operators) { operator ->
                    OperatorCard(operator)
                }
            }
        }
    }
}

// --- LA CARD (DISEÑO) ---
@Composable
fun OperatorCard(operator: Operator) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFF1E1E1E) // Gris un poco más claro que el fondo
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // IMAGEN CON COIL
            val fullImageUrl = if (operator.imageUrl.startsWith("http")) {
                operator.imageUrl
            } else {
                "http://10.0.2.2:8080${operator.imageUrl}"
            }

            AsyncImage(
                model = fullImageUrl,
                contentDescription = operator.name,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.DarkGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = operator.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(
                    text = "${operator.operatorClass} | ${operator.element}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
                Text(
                    text = operator.rarity,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFFFD700) // Color Dorado para las estrellas
                )
            }
        }
    }
}
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
