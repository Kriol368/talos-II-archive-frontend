package com.endfield.talosIIarchive.ui.screens.wiki

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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



    Box(modifier = Modifier.fillMaxSize().background(TechBackground)) {
        if (operatorViewModel.isLoading && operators.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = EndfieldOrange
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
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
                            // 3. Modificamos el orden y se lo devolvemos al ViewModel
                            val mutableList = operators.toMutableList()
                            mutableList.add(toIndex, mutableList.removeAt(fromIndex))
                            operatorViewModel.updateOrder(mutableList)
                        },
                        index = index,
                        totalItems = operators.size
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
        modifier: Modifier = Modifier,
        onClick: () -> Unit,
        onMove: (Int, Int) -> Unit
    ) {
        val rarityColor = if (operator.rarity.contains("6")) EndfieldOrange else EndfieldCyan
        val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current // Para vibración

        // Estados de control
        var isDragging by remember { mutableStateOf(false) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
                .zIndex(if (isDragging) 1f else 0f)
                .graphicsLayer {
                    // Usamos graphicsLayer para mejor rendimiento en animaciones
                    translationX = offset.x
                    translationY = offset.y
                    scaleX = if (isDragging) 1.05f else 1f
                    scaleY = if (isDragging) 1.05f else 1f
                    shadowElevation = if (isDragging) 20f else 0f
                }
                .border(
                    if (isDragging) 2.dp else 0.5.dp,
                    if (isDragging) EndfieldYellow else TechBorder
                )
                .background(TechSurface)
                .pointerInput(index) { // Re-inicializar cuando el índice cambia
                    detectDragGesturesAfterLongPress(
                        onDragStart = {
                            isDragging = true
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        },
                        onDragEnd = { isDragging = false; offset = Offset.Zero },
                        onDragCancel = { isDragging = false; offset = Offset.Zero },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            offset += dragAmount

                            // LÓGICA DE SWAP PARA GRID DE 2 COLUMNAS
                            // Umbrales basados en el tamaño de la card (aproximado)
                            val thresholdY = 300f
                            val thresholdX = 250f

                            // Mover hacia abajo (+2 posiciones)
                            if (offset.y > thresholdY && index + 2 < totalItems) {
                                onMove(index, index + 2)
                                offset = offset.copy(y = offset.y - thresholdY)
                            }
                            // Mover hacia arriba (-2 posiciones)
                            else if (offset.y < -thresholdY && index - 2 >= 0) {
                                onMove(index, index - 2)
                                offset = offset.copy(y = offset.y + thresholdY)
                            }
                            // Mover lateralmente (+1 o -1)
                            else if (offset.x > thresholdX && index % 2 == 0 && index + 1 < totalItems) {
                                onMove(index, index + 1)
                                offset = offset.copy(x = offset.x - thresholdX)
                            }
                            else if (offset.x < -thresholdX && index % 2 != 0 && index - 1 >= 0) {
                                onMove(index, index - 1)
                                offset = offset.copy(x = offset.x + thresholdX)
                            }
                        }
                    )
                }
                .clickable(enabled = !isDragging) { onClick() }
        ) {
            // --- CONTENIDO IGUAL ---
            AsyncImage(
                model = "http://158.179.216.16:8080${operator.imageUrl}",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().alpha(if (isDragging) 0.7f else 0.85f)
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



//
//@Composable
//fun OperatorGridItem(operator: Operator, onClick: () -> Unit) {
//    val rarityColor = if (operator.rarity.contains("6")) EndfieldOrange else EndfieldCyan
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .aspectRatio(0.7f)
//            .border(0.5.dp, TechBorder)
//            .background(TechSurface)
//            .clickable { onClick() }
//    ) {
//        AsyncImage(
//            model = if (operator.imageUrl.startsWith("http")) operator.imageUrl
//            else "http://158.179.216.16:8080${operator.imageUrl}",
//            contentDescription = null,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .fillMaxSize()
//                .alpha(0.85f)
//        )
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    Brush.verticalGradient(
//                        listOf(Color.Transparent, Color.Black.copy(0.8f)),
//                        startY = 400f
//                    )
//                )
//        )
//
//        Column(modifier = Modifier
//            .align(Alignment.BottomStart)
//            .padding(12.dp)) {
//            Text(
//                operator.rarity,
//                color = rarityColor,
//                fontSize = 11.sp,
//                fontWeight = FontWeight.Bold
//            )
//            Text(
//                operator.name.uppercase(),
//                color = Color.White,
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Black,
//                lineHeight = 20.sp
//            )
//        }
//
//        Text(
//            "№ ${operator.id}",
//            color = Color.White.copy(0.3f),
//            fontSize = 9.sp,
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(8.dp)
//        )
//    }
//}