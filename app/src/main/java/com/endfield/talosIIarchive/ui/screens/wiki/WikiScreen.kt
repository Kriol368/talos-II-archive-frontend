    package com.endfield.talosIIarchive.ui.screens.wiki

    import androidx.compose.animation.core.LinearEasing
    import androidx.compose.animation.core.RepeatMode
    import androidx.compose.animation.core.animateFloat
    import androidx.compose.animation.core.infiniteRepeatable
    import androidx.compose.animation.core.rememberInfiniteTransition
    import androidx.compose.animation.core.tween
    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxHeight
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.width
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.automirrored.filled.ArrowBack
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Surface
    import androidx.compose.material3.Text
    import androidx.compose.material3.TopAppBar
    import androidx.compose.material3.TopAppBarDefaults
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Brush
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.graphics.graphicsLayer
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import com.endfield.talosIIarchive.domain.models.Gear
    import com.endfield.talosIIarchive.domain.models.Operator
    import com.endfield.talosIIarchive.domain.models.Weapon
    import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
    import com.endfield.talosIIarchive.ui.theme.EndfieldOrange
    import com.endfield.talosIIarchive.ui.theme.EndfieldYellow
    import com.endfield.talosIIarchive.ui.theme.TechBackground
    import com.endfield.talosIIarchive.ui.theme.TechBorder
    import com.endfield.talosIIarchive.ui.theme.TechSurface
    import com.endfield.talosIIarchive.ui.viewmodel.GearViewModel
    import com.endfield.talosIIarchive.ui.viewmodel.OperatorViewModel
    import com.endfield.talosIIarchive.ui.viewmodel.WeaponViewModel

    enum class WikiCategory {
        OPERATORS, WEAPONS, GEAR
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WikiScreen(
        operatorViewModel: OperatorViewModel,
        weaponViewModel: WeaponViewModel,
        gearViewModel: GearViewModel
    ) {
        var selectedCategory by remember { mutableStateOf<WikiCategory?>(null) }

        // Estado para abrir la ficha de operador al instante
        var previewOperator by remember { mutableStateOf<Operator?>(null) }
        var previewWeapon by remember { mutableStateOf<Weapon?>(null) }
        var previewGear by remember { mutableStateOf<Gear?>(null) }


        Box(modifier = Modifier.fillMaxSize()) {

            // --- CAPA 1: BASE (MENÚ O LISTAS) ---
            Surface(modifier = Modifier.fillMaxSize(), color = TechBackground) {
                if (selectedCategory != null) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "// ${selectedCategory.toString()}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    letterSpacing = 1.sp
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { selectedCategory = null }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
                                        tint = EndfieldOrange
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = TechSurface,
                                titleContentColor = Color.White
                            )
                        )

                        when (selectedCategory) {
                            WikiCategory.OPERATORS -> {
                                OperatorListScreen(operatorViewModel) { op ->
                                    // Acción Optimista: Seteamos preview y disparamos fetch
                                    previewOperator = op
                                    operatorViewModel.fetchOperatorDetails(op.id)
                                }
                            }
                            WikiCategory.WEAPONS -> {
                                WeaponListScreen(weaponViewModel) { weapon ->
                                    previewWeapon = weapon
                                    weaponViewModel.fetchWeaponDetails(weapon.id)
                                }
                            }
                            WikiCategory.GEAR -> {
                                GearListScreen(gearViewModel) { gear ->
                                    previewGear = gear
                                    gearViewModel.fetchGearDetails(gear.id)
                                }
                            }
                            else -> {}
                        }
                    }
                } else {
                    // 1. RELOJ GLOBAL: Va de -1 a 3 (para que la luz entre desde arriba y salga por abajo)
                    val infiniteTransition = rememberInfiniteTransition(label = "NeonWave")
                    val pulsePosition by infiniteTransition.animateFloat(
                        initialValue = -1f,
                        targetValue = 3f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(5000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ), label = "WaveProgress"
                    )

                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text(
                            text = "// ARCHIVE_SYSTEM_V.2.0",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Botón 0
                        WikiMenuButton(
                            "01",
                            "OPERATORS",
                            EndfieldOrange,
                            0f,
                            pulsePosition,
                            Modifier.weight(1f)
                        ) {
                            selectedCategory = WikiCategory.OPERATORS
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Botón 1
                        WikiMenuButton(
                            "02",
                            "WEAPONS",
                            EndfieldCyan,
                            1f,
                            pulsePosition,
                            Modifier.weight(1f)
                        ) {
                            selectedCategory = WikiCategory.WEAPONS
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Botón 2
                        WikiMenuButton(
                            "03",
                            "GEAR",
                            EndfieldYellow,
                            2f,
                            pulsePosition,
                            Modifier.weight(1f)
                        ) {
                            selectedCategory = WikiCategory.GEAR
                        }

                    }
                }
            }

            // --- CAPA 2: PANTALLAS DE DETALLES (OVERLAYS) ---

            // Detalle de Operador (Optimista: usa preview mientras carga el full)
            val operatorToShow = operatorViewModel.selectedOperatorFull ?: previewOperator
            operatorToShow?.let { op ->
                OperatorDetailScreen(
                    operator = op,
                    isLoadingFullData = operatorViewModel.isDetailLoading
                ) {
                    previewOperator = null
                    operatorViewModel.clearSelectedOperator()
                }
            }

            val weaponToShow = weaponViewModel.selectedWeaponFull ?: previewWeapon
            weaponToShow?.let {wp ->
                WeaponDetailScreen(
                    weapon = wp,
                    isLoadingFullData = weaponViewModel.isDetailLoading
                ) {
                    previewWeapon = null
                    weaponViewModel.clearSelectedWeapon()
                }

            }


            val gearToShow = gearViewModel.selectedGearFull ?: previewGear
            gearToShow?.let {gear ->
                GearDetailScreen(
                    gear = gear,
                    isLoadingFullData = gearViewModel.isDetailLoading
                ) {
                    previewGear = null
                    gearViewModel.clearSelectedGear()
                }

            }

        }
    }
    @Composable
    fun WikiMenuButton(
        number: String,
        title: String,
        accentColor: Color,
        buttonIndex: Float,    // Su posición (0, 1 o 2)
        currentWave: Float,    // Dónde está la luz ahora
        modifier: Modifier,
        onClick: () -> Unit
    ) {
        // CÁLCULO DE INTENSIDAD (Efecto Degradado)
        // Calculamos la distancia y la convertimos en un valor de 0 a 1
        // Cuanto más cerca esté currentWave de buttonIndex, más brilla.
        val distance = Math.abs(currentWave - buttonIndex)
        val intensity = (1f - distance).coerceIn(0f, 1f) // 0.2f es el brillo mínimo (apagado)
        val textAlpha = (0.7f + (intensity * 0.3f)).coerceIn(0.7f, 1f)
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(TechSurface)
                .clickable { onClick() }
        ) {
            // --- LA BARRA NEÓN ---
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    // La barra sí brilla intensamente (de 0.2 a 1.0)
                    .background(accentColor.copy(alpha = (0.2f + intensity * 0.8f)))
            )

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.CenterStart)
            ) {
                Text(
                    text = "ID.$number",
                    color = accentColor.copy(alpha = intensity),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                // --- TÍTULO PRINCIPAL ---
                Text(
                    text = title,
                    color = Color.White, // Siempre blanco puro
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    modifier = Modifier.graphicsLayer {
                        // Solo animamos un poco la opacidad para que "respire",
                        // pero se mantiene siempre legible (mínimo 0.7)
                        alpha = textAlpha
                    }
                )
            }

            // Decoración técnica en la esquina
            Text(
                text = "ACCESS >",
                color = Color.White.copy(alpha = 0.2f + (intensity * 0.3f)),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }