package com.endfield.talosIIarchive.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// Creamos un Modifier reutilizable
fun Modifier.swipeToDismiss(
    onDismiss: () -> Unit,
    scrollState: ScrollState
): Modifier = composed {

    val coroutineScope = rememberCoroutineScope()
    // Usamos Animatable para poder controlar manualmente la animación de salida
    val offsetY = remember { Animatable(0f) }
    var isDismissing by remember { mutableStateOf(false) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (isDismissing) return Offset.Zero

                if (available.y > 0 && scrollState.value == 0) {
                    coroutineScope.launch {
                        offsetY.snapTo(offsetY.value + available.y * 0.4f)
                    }
                    return Offset(0f, available.y)
                }
                if (available.y < 0 && offsetY.value > 0) {
                    coroutineScope.launch {
                        val newOffset = (offsetY.value + available.y).coerceAtLeast(0f)
                        offsetY.snapTo(newOffset)
                    }
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: androidx.compose.ui.unit.Velocity): androidx.compose.ui.unit.Velocity {
                if (isDismissing) return super.onPreFling(available)

                if (available.y < -500f) {
                    // Intento de rectificación hacia arriba: volver a 0
                    offsetY.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                } else if (offsetY.value > 500f || available.y > 3000f) {
                    // ¡CERRAR! Primero animamos hacia abajo y luego avisamos al sistema
                    coroutineScope.launch {
                        // Animamos hasta 2000px para que la pantalla salga de la vista
                        offsetY.animateTo(
                            targetValue = 3000f,
                            animationSpec = spring(stiffness = Spring.StiffnessLow)
                        )
                        onDismiss() // Solo cerramos cuando la pantalla ya no se ve
                    }
                } else {
                    // No llegó al límite: volver a su sitio
                    offsetY.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                }
                return super.onPreFling(available)
            }
        }
    }

    this
        .nestedScroll(nestedScrollConnection)
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown()
                // Esperamos a que el usuario suelte todos los dedos
                while (true) {
                    val event = awaitPointerEvent()
                    val allUp = event.changes.all { !it.pressed }
                    if (allUp) {
                        // Al soltar, si no estamos cerrando, aseguramos el reset
                        if (!isDismissing && offsetY.value > 0f) {
                            coroutineScope.launch {
                                offsetY.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                            }
                        }
                        break
                    }
                }
            }
        }
        .offset {
            // Usamos roundToInt para evitar saltos visuales
            IntOffset(0, offsetY.value.roundToInt())
        }
        .graphicsLayer {
            // EFECTO EXTRA: Evita que la pantalla se vea "negra" de forma fea
            // a medida que baja, se vuelve un poco transparente
            alpha = (1f - (offsetY.value / 2000f)).coerceIn(0f, 1f)
        }
}