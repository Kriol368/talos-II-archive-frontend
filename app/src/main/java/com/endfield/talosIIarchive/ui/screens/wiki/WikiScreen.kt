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

        Surface(modifier = Modifier.fillMaxSize(), color = TechBackground) {
            when {
                operatorViewModel.isDetailLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = EndfieldOrange)
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "LOADING_PERSONNEL_FILE...",
                                color = EndfieldOrange,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }

                weaponViewModel.isDetailLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = EndfieldCyan)
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "LOADING_WEAPON_FILE...",
                                color = EndfieldCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }

                gearViewModel.isDetailLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "LOADING_GEAR_FILE...",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }

                operatorViewModel.selectedOperatorFull != null -> {
                    OperatorDetailScreen(operatorViewModel.selectedOperatorFull!!) {
                        operatorViewModel.clearSelectedOperator()
                    }
                }

                weaponViewModel.selectedWeapon != null -> {
                    WeaponDetailScreen(weaponViewModel.selectedWeapon!!) {
                        weaponViewModel.clearSelectedWeapon()
                    }
                }

                gearViewModel.selectedGear != null -> {
                    GearDetailScreen(gearViewModel.selectedGear!!) {
                        gearViewModel.clearSelectedGear()
                    }
                }

                selectedCategory != null -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "// ${selectedCategory.toString()}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    letterSpacing = 1.sp
                                )
                            }, navigationIcon = {
                                IconButton(onClick = { selectedCategory = null }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
                                        tint = EndfieldOrange
                                    )
                                }
                            }, colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = TechSurface, titleContentColor = Color.White
                            )
                        )

                        when (selectedCategory) {
                            WikiCategory.OPERATORS -> {
                                OperatorListScreen(operatorViewModel) { operator ->
                                    operatorViewModel.fetchOperatorDetails(operator.id)
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
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
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

