package com.endfield.talosIIarchive.ui.screens.wiki

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.endfield.talosIIarchive.domain.models.Operator
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldOrange
import com.endfield.talosIIarchive.ui.theme.EndfieldYellow
import com.endfield.talosIIarchive.ui.theme.TechBackground
import com.endfield.talosIIarchive.ui.theme.TechBorder
import com.endfield.talosIIarchive.ui.theme.TechSurface
import com.endfield.talosIIarchive.ui.viewmodel.OperatorViewModel
import kotlin.math.roundToInt

@Composable
fun OperatorListScreen(
    operatorViewModel: OperatorViewModel,
    onOperatorClick: (Operator) -> Unit
) {
    LaunchedEffect(Unit) {
        if (operatorViewModel.operators.isEmpty()) {
            operatorViewModel.fetchOperators()
        }
    }

    val operators = operatorViewModel.operators
    val configuration = LocalConfiguration.current

    // Determinamos columnas dinámicamente: 2 en vertical, 4 en horizontal
    val columnCount = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 2

    Box(modifier = Modifier.fillMaxSize().background(TechBackground)) {
        if (operatorViewModel.isLoading && operators.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = EndfieldOrange
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columnCount),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(operators, key = { _, op -> op.id }) { index, operator ->
                    OperatorGridItem(
                        operator = operator,
                        modifier = Modifier.animateItem(),
                        onClick = { onOperatorClick(operator) },
                        onMove = { fromIndex, toIndex ->
                            val mutableList = operators.toMutableList()
                            if (fromIndex in mutableList.indices && toIndex in mutableList.indices) {
                                mutableList.add(toIndex, mutableList.removeAt(fromIndex))
                                operatorViewModel.updateOrder(mutableList)
                            }
                        },
                        index = index,
                        totalItems = operators.size,
                        columnCount = columnCount
                    )
                }
            }
        }
    }
}

@Composable
fun OperatorGridItem(
    operator: Operator,
    index: Int,
    totalItems: Int,
    columnCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onMove: (Int, Int) -> Unit
) {
    val rarityColor = if (operator.rarity.contains("6")) EndfieldOrange else EndfieldCyan
    val haptic = LocalHapticFeedback.current

    var isDragging by remember { mutableStateOf(false) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .zIndex(if (isDragging) 1f else 0f)
            .graphicsLayer {
                translationX = offset.x
                translationY = offset.y
                scaleX = if (isDragging) 1.05f else 1f
                scaleY = if (isDragging) 1.05f else 1f
            }
            .border(
                if (isDragging) 2.dp else 0.5.dp,
                if (isDragging) EndfieldYellow else TechBorder
            )
            .background(TechSurface)
            .pointerInput(index, columnCount) { // Escuchar cambios de columna/index
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        isDragging = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onDragEnd = { isDragging = false; offset = Offset.Zero },
                    onDragCancel = { isDragging = false; offset = Offset.Zero },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset += dragAmount

                        // LÓGICA DE SWAP ADAPTATIVA
                        val thresholdY = size.height.toFloat() * 0.8f
                        val thresholdX = size.width.toFloat() * 0.8f

                        // Mover Vertical (Salto de filas según columnCount)
                        if (offset.y > thresholdY && index + columnCount < totalItems) {
                            onMove(index, index + columnCount)
                            offset = offset.copy(y = offset.y - thresholdY)
                        } else if (offset.y < -thresholdY && index - columnCount >= 0) {
                            onMove(index, index - columnCount)
                            offset = offset.copy(y = offset.y + thresholdY)
                        }

                        // Mover Horizontal (Solo si no se sale de su fila actual)
                        val currentRow = index / columnCount
                        if (offset.x > thresholdX && index + 1 < totalItems && (index + 1) / columnCount == currentRow) {
                            onMove(index, index + 1)
                            offset = offset.copy(x = offset.x - thresholdX)
                        } else if (offset.x < -thresholdX && index - 1 >= 0 && (index - 1) / columnCount == currentRow) {
                            onMove(index, index - 1)
                            offset = offset.copy(x = offset.x + thresholdX)
                        }
                    }
                )
            }
            .clickable(enabled = !isDragging) { onClick() }
    ) {
        // --- RENDERIZADO DE IMAGEN ---
        AsyncImage(
            model = "http://158.179.216.16:8080${operator.imageUrl}",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().alpha(if (isDragging) 0.7f else 0.85f)
        )

        // Gradiente técnico
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(0.8f)),
                        startY = 300f
                    )
                )
        )

        // Info del Operador
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
                fontSize = 16.sp, // Un poco más pequeña para landscape
                fontWeight = FontWeight.Black,
                lineHeight = 18.sp
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