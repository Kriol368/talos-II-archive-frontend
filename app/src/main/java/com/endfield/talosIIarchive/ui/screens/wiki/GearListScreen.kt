package com.endfield.talosIIarchive.ui.screens.wiki

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.endfield.talosIIarchive.domain.models.Gear
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldYellow
import com.endfield.talosIIarchive.ui.theme.TechBackground
import com.endfield.talosIIarchive.ui.theme.TechBorder
import com.endfield.talosIIarchive.ui.theme.TechSurface
import com.endfield.talosIIarchive.ui.viewmodel.GearViewModel

@Composable
fun GearListScreen(
    gearViewModel: GearViewModel,
    onGearClick: (Gear) -> Unit
) {
    LaunchedEffect(Unit) {
        gearViewModel.fetchGear()
    }

    Box(modifier = Modifier.fillMaxSize().background(TechBackground)) {
        if (gearViewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = EndfieldYellow
            )
        } else if (gearViewModel.gearList.isEmpty()) {
            Text("DATA_NOT_FOUND", color = Color.Red, modifier = Modifier.align(Alignment.Center))
        } else {
            // Cambio a Grid de 2 columnas para igualar a los Operadores
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(gearViewModel.gearList) { gear ->
                    GearGridItem(gear) { onGearClick(gear) }
                }
            }
        }
    }
}

@Composable
fun GearGridItem(gear: Gear, onClick: () -> Unit) {
    // Usamos el amarillo de Endfield para resaltar el Set del equipo
    val accentColor = EndfieldYellow

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f) // Misma proporción vertical que los operadores
            .border(0.5.dp, TechBorder)
            .background(TechSurface)
            .clickable { onClick() }
    ) {
        // Imagen de fondo con opacidad reducida
        AsyncImage(
            model = if (gear.imageUrl.startsWith("http")) gear.imageUrl
            else "http://158.179.216.16:8080${gear.imageUrl}",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.85f)
        )

        // Degradado inferior para legibilidad del texto
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(0.8f)),
                        startY = 400f
                    )
                )
        )

        // Información en la parte inferior
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = gear.gearSet.uppercase(),
                color = accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = gear.name.uppercase(),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 18.sp
            )
            Text(
                text = gear.gearType,
                color = Color.LightGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // ID del objeto en la esquina superior derecha
        Text(
            text = "№ ${gear.id}",
            color = Color.White.copy(0.3f),
            fontSize = 9.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
    }
}