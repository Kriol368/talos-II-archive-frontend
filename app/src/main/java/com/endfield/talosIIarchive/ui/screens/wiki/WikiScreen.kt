    package com.endfield.talosIIarchive.ui.screens.wiki

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
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import com.endfield.talosIIarchive.domain.models.Operator
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
                                    weaponViewModel.fetchWeaponDetails(weapon.id)
                                }
                            }
                            WikiCategory.GEAR -> {
                                GearListScreen(gearViewModel) { gear ->
                                    gearViewModel.fetchGearDetails(gear.id)
                                }
                            }
                            else -> {}
                        }
                    }
                } else {
                    // MENÚ PRINCIPAL
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text(
                            text = "// ARCHIVE_SYSTEM_V.2.0",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        WikiMenuButton("01", "OPERATORS", EndfieldOrange, Modifier.weight(1f)) {
                            selectedCategory = WikiCategory.OPERATORS
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        WikiMenuButton("02", "WEAPONS", EndfieldCyan, Modifier.weight(1f)) {
                            selectedCategory = WikiCategory.WEAPONS
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        WikiMenuButton("03", "GEAR", EndfieldYellow, Modifier.weight(1f)) {
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

            // Detalle de Arma
            weaponViewModel.selectedWeapon?.let { weapon ->
                WeaponDetailScreen(weapon) {
                    weaponViewModel.clearSelectedWeapon()
                }
            }

            // Detalle de Gear
            gearViewModel.selectedGear?.let { gear ->
                GearDetailScreen(gear) {
                    gearViewModel.clearSelectedGear()
                }
            }

            // --- CAPA 3: CARGAS NO OPTIMISTAS (PARA WEAPONS/GEAR) ---
            val loadingState = when {
                // No incluimos operators aquí para que se vea la ficha en lugar del overlay negro
                weaponViewModel.isDetailLoading -> EndfieldCyan to "LOADING_WEAPON_FILE..."
                gearViewModel.isDetailLoading -> Color.White to "LOADING_GEAR_FILE..."
                else -> null
            }

            loadingState?.let { (color, text) ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = color)
                        Spacer(Modifier.height(16.dp))
                        Text(text = text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    @Composable
    fun WikiMenuButton(
        number: String, title: String, accentColor: Color, modifier: Modifier, onClick: () -> Unit
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .border(1.dp, TechBorder)
                .background(TechSurface)
                .clickable { onClick() }) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(accentColor)
            )

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.CenterStart)
            ) {
                Text(
                    text = "ID.$number",
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                )
            }

            Text(
                text = "ACCESS >",
                color = Color.DarkGray,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                fontSize = 10.sp
            )
        }
    }

