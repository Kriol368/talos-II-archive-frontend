package com.endfield.talosIIarchive.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.endfield.talosIIarchive.ui.theme.*
import com.endfield.talosIIarchive.ui.viewmodel.TeamViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTeamScreen(
    teamViewModel: TeamViewModel,
    newTeamViewModel: NewTeamViewModel,
    onBack: () -> Unit,
    onCreateSuccess: () -> Unit
) {
    val viewModel = newTeamViewModel
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "CREATE NEW TEAM",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TechSurface,
                    titleContentColor = Color.White
                ),
                actions = {
                    if (viewModel.isFormValid()) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    val success = viewModel.createTeam(teamViewModel)
                                    if (success) {
                                        showSuccessDialog = true
                                    }
                                }
                            },
                            enabled = !viewModel.isCreating
                        ) {
                            if (viewModel.isCreating) {
                                CircularProgressIndicator(
                                    color = EndfieldPurple,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Create Team",
                                    tint = EndfieldPurple
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(TechBackground)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Team Info Section
                item {
                    TeamInfoSection(viewModel)
                }

                // Operators Section
                item {
                    Text(
                        "SELECT OPERATORS (4 REQUIRED)",
                        color = EndfieldPurple,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(4) { index ->
                    OperatorSelectorCard(
                        index = index,
                        viewModel = viewModel
                    )
                }

                // Weapons Section (only shown when all operators are selected)
                if (viewModel.selectedOperators.size == 4 && viewModel.selectedOperators.all { it != null }) {
                    item {
                        Text(
                            "SELECT WEAPONS",
                            color = EndfieldCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                        )
                    }

                    items(4) { index ->
                        WeaponSelectorCard(
                            index = index,
                            viewModel = viewModel
                        )
                    }
                }

                // Gear Section (only shown when all weapons are selected)
                if (viewModel.selectedWeapons.size == 4 && viewModel.selectedWeapons.all { it != null }) {
                    item {
                        Text(
                            "SELECT GEAR FOR EACH OPERATOR",
                            color = EndfieldYellow,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                        )
                    }

                    items(4) { operatorIndex ->
                        GearSelectorSection(
                            operatorIndex = operatorIndex,
                            viewModel = viewModel
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            // Loading State
            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = EndfieldPurple)
                        Text(
                            "LOADING DATA...",
                            color = EndfieldPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Selection Dialogs
    if (viewModel.showingOperatorDialogFor != null) {
        OperatorSelectionDialog(
            title = "SELECT OPERATOR",
            items = viewModel.availableOperatorsForSelection(),
            onOperatorSelected = { operatorItem ->
                viewModel.selectOperator(viewModel.showingOperatorDialogFor!!, operatorItem.operator)
                viewModel.showingOperatorDialogFor = null
            },
            onDismiss = { viewModel.showingOperatorDialogFor = null }
        )
    }

    if (viewModel.showingWeaponDialogFor != null) {
        WeaponSelectionDialog(
            title = "SELECT WEAPON FOR ${viewModel.selectedOperators[viewModel.showingWeaponDialogFor!!]?.name?.uppercase() ?: "OPERATOR"}",
            items = viewModel.availableWeaponsForSelection(viewModel.showingWeaponDialogFor!!),
            onWeaponSelected = { weaponItem ->
                viewModel.selectWeapon(viewModel.showingWeaponDialogFor!!, weaponItem.weapon)
                viewModel.showingWeaponDialogFor = null
            },
            onDismiss = { viewModel.showingWeaponDialogFor = null }
        )
    }

    if (viewModel.showingGearDialogFor != null) {
        val (operatorIndex, gearType) = viewModel.showingGearDialogFor!!
        GearSelectionDialog(
            title = "SELECT ${gearType.name.uppercase()} FOR ${viewModel.selectedOperators[operatorIndex]?.name?.uppercase() ?: "OPERATOR"}",
            items = viewModel.availableGearForSelection(gearType),
            onGearSelected = { gearItem ->
                viewModel.selectGear(operatorIndex, gearType, gearItem.gear)
                viewModel.showingGearDialogFor = null
            },
            onDismiss = { viewModel.showingGearDialogFor = null }
        )
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("TEAM CREATED!", color = EndfieldPurple, fontWeight = FontWeight.Bold) },
            text = { Text("Your team has been created successfully!") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        onCreateSuccess()
                    }
                ) {
                    Text("OK", color = EndfieldPurple)
                }
            },
            containerColor = TechSurface
        )
    }
}

@Composable
fun TeamInfoSection(viewModel: NewTeamViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TechSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "TEAM INFORMATION",
                color = EndfieldPurple,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = viewModel.teamName,
                onValueChange = { viewModel.teamName = it },
                label = { Text("Team Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EndfieldPurple,
                    unfocusedBorderColor = TechBorder,
                    focusedLabelColor = EndfieldPurple,
                    unfocusedLabelColor = Color.Gray
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EndfieldPurple,
                    unfocusedBorderColor = TechBorder,
                    focusedLabelColor = EndfieldPurple,
                    unfocusedLabelColor = Color.Gray
                ),
                maxLines = 3
            )
        }
    }
}

@Composable
fun OperatorSelectorCard(index: Int, viewModel: NewTeamViewModel) {
    val operator = viewModel.selectedOperators.getOrNull(index)
    val backgroundColor = if (operator != null) EndfieldPurple.copy(alpha = 0.2f) else TechSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.showingOperatorDialogFor = index },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "OPERATOR ${index + 1}",
                    color = if (operator != null) EndfieldPurple else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    operator?.name ?: "Select Operator",
                    color = if (operator != null) Color.White else Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = if (operator != null) FontWeight.Bold else FontWeight.Normal
                )
                if (operator != null) {
                    Text(
                        "${operator.operatorClass} • ${operator.weaponType}",
                        color = EndfieldPurple.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }

            if (operator != null) {
                Box(
                    modifier = Modifier
                        .background(EndfieldPurple, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "SELECTED",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun WeaponSelectorCard(index: Int, viewModel: NewTeamViewModel) {
    val weapon = viewModel.selectedWeapons.getOrNull(index)
    val operator = viewModel.selectedOperators.getOrNull(index)
    val isSelectable = operator != null
    val backgroundColor = if (weapon != null) EndfieldCyan.copy(alpha = 0.2f) else TechSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isSelectable) {
                if (isSelectable) viewModel.showingWeaponDialogFor = index
            },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "WEAPON FOR ${operator?.name?.uppercase() ?: "OPERATOR ${index + 1}"}",
                    color = if (weapon != null) EndfieldCyan else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    weapon?.name ?: "Select Weapon",
                    color = if (weapon != null) Color.White else Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = if (weapon != null) FontWeight.Bold else FontWeight.Normal
                )
                if (weapon != null) {
                    Text(
                        weapon.weaponType,
                        color = EndfieldCyan.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                } else if (operator != null) {
                    Text(
                        "Must be ${operator.weaponType} type",
                        color = EndfieldCyan.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            if (weapon != null) {
                Box(
                    modifier = Modifier
                        .background(EndfieldCyan, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "SELECTED",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun GearSelectorSection(operatorIndex: Int, viewModel: NewTeamViewModel) {
    val operator = viewModel.selectedOperators.getOrNull(operatorIndex)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TechSurface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "GEAR FOR ${operator?.name?.uppercase() ?: "OPERATOR ${operatorIndex + 1}"}",
                color = EndfieldYellow,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            // Armor
            GearSelectorRow(
                gearType = GearType.ARMOR,
                selectedGear = viewModel.selectedGear[operatorIndex]?.get(GearType.ARMOR),
                onClick = {
                    viewModel.showingGearDialogFor = Pair(operatorIndex, GearType.ARMOR)
                }
            )

            // Gloves
            GearSelectorRow(
                gearType = GearType.GLOVES,
                selectedGear = viewModel.selectedGear[operatorIndex]?.get(GearType.GLOVES),
                onClick = {
                    viewModel.showingGearDialogFor = Pair(operatorIndex, GearType.GLOVES)
                }
            )

            // Kit 1
            GearSelectorRow(
                gearType = GearType.KIT1,
                selectedGear = viewModel.selectedGear[operatorIndex]?.get(GearType.KIT1),
                onClick = {
                    viewModel.showingGearDialogFor = Pair(operatorIndex, GearType.KIT1)
                }
            )

            // Kit 2
            GearSelectorRow(
                gearType = GearType.KIT2,
                selectedGear = viewModel.selectedGear[operatorIndex]?.get(GearType.KIT2),
                onClick = {
                    viewModel.showingGearDialogFor = Pair(operatorIndex, GearType.KIT2)
                }
            )
        }
    }
}

@Composable
fun GearSelectorRow(gearType: GearType, selectedGear: com.endfield.talosIIarchive.domain.models.Gear?, onClick: () -> Unit) {
    val backgroundColor = if (selectedGear != null) EndfieldYellow.copy(alpha = 0.2f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, shape = RoundedCornerShape(6.dp))
            .border(1.dp, TechBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                gearType.name,
                color = if (selectedGear != null) EndfieldYellow else Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                selectedGear?.name ?: "Select ${gearType.name}",
                color = if (selectedGear != null) Color.White else Color.Gray,
                fontSize = 14.sp
            )
        }

        if (selectedGear != null) {
            Text(
                "✓",
                color = EndfieldYellow,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OperatorSelectionDialog(
    title: String,
    items: List<SelectionItem.OperatorItem>,
    onOperatorSelected: (SelectionItem.OperatorItem) -> Unit,
    onDismiss: () -> Unit
) {
    SelectionDialog(
        title = title,
        items = items,
        itemName = { it.operator.name },
        onItemSelected = onOperatorSelected,
        onDismiss = onDismiss
    )
}

@Composable
fun WeaponSelectionDialog(
    title: String,
    items: List<SelectionItem.WeaponItem>,
    onWeaponSelected: (SelectionItem.WeaponItem) -> Unit,
    onDismiss: () -> Unit
) {
    SelectionDialog(
        title = title,
        items = items,
        itemName = { it.weapon.name },
        onItemSelected = onWeaponSelected,
        onDismiss = onDismiss
    )
}

@Composable
fun GearSelectionDialog(
    title: String,
    items: List<SelectionItem.GearItem>,
    onGearSelected: (SelectionItem.GearItem) -> Unit,
    onDismiss: () -> Unit
) {
    SelectionDialog(
        title = title,
        items = items,
        itemName = { it.gear.name },
        onItemSelected = onGearSelected,
        onDismiss = onDismiss
    )
}

@Composable
fun <T> SelectionDialog(
    title: String,
    items: List<T>,
    itemName: (T) -> String,
    onItemSelected: (T) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = TechSurface
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    title,
                    color = EndfieldPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                Divider(color = TechBorder, thickness = 1.dp)

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(items) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onItemSelected(item) }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                itemName(item),
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        Divider(color = TechBorder.copy(alpha = 0.3f), thickness = 0.5.dp)
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("CANCEL", color = Color.Gray)
                }
            }
        }
    }
}