package com.endfield.talosIIarchive.ui.screens.wiki

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.endfield.talosIIarchive.domain.models.Operator
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldOrange
import com.endfield.talosIIarchive.ui.theme.TechBorder
import com.endfield.talosIIarchive.ui.theme.TechSurface
import com.endfield.talosIIarchive.ui.viewmodel.OperatorViewModel

@Composable
fun OperatorListScreen(
    operatorViewModel: OperatorViewModel,
    onOperatorClick: (Operator) -> Unit
) {
    LaunchedEffect(Unit) {
        operatorViewModel.fetchOperators()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (operatorViewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = EndfieldOrange
            )
        } else if (operatorViewModel.operators.isEmpty()) {
            Text("DATA_NOT_FOUND", color = Color.Red, modifier = Modifier.align(Alignment.Center))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(operatorViewModel.operators) { operator ->
                    OperatorGridItem(operator) { onOperatorClick(operator) }
                }
            }
        }
    }
}

@Composable
fun OperatorGridItem(operator: Operator, onClick: () -> Unit) {
    val rarityColor = if (operator.rarity.contains("6")) EndfieldOrange else EndfieldCyan

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .border(0.5.dp, TechBorder)
            .background(TechSurface)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = if (operator.imageUrl.startsWith("http")) operator.imageUrl
            else "http://10.0.2.2:8080${operator.imageUrl}",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.85f)
        )

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

        Column(modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(12.dp)) {
            Text(
                operator.rarity,
                color = rarityColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                operator.name.uppercase(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 20.sp
            )
        }

        Text(
            "№ ${operator.id}",
            color = Color.White.copy(0.3f),
            fontSize = 9.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
    }
}