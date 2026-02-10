package com.endfield.talosIIarchive.ui.screens.social

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.endfield.talosIIarchive.domain.models.Gear
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldPurple
import com.endfield.talosIIarchive.ui.theme.EndfieldYellow
import com.endfield.talosIIarchive.ui.theme.TechBackground
import com.endfield.talosIIarchive.ui.theme.TechBorder
import com.endfield.talosIIarchive.ui.theme.TechSurface
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
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        newTeamViewModel.loadAllData()
    }

    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "// NEW_TEAM_CREATION",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TechSurface,
                    titleContentColor = Color.White
                ),
                actions = {
                    if (newTeamViewModel.isFormValid()) {
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .background(Color.Black)
                                .clickable(
                                    enabled = !newTeamViewModel.isCreating,
                                    onClick = {
                                        coroutineScope.launch {
                                            val success = newTeamViewModel.createTeam(teamViewModel)
                                            if (success) {
                                                showSuccessDialog = true
                                            }
                                        }
                                    }
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            if (newTeamViewModel.isCreating) {
                                CircularProgressIndicator(
                                    color = EndfieldPurple,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "CREATE",
                                    color = EndfieldPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
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
                item {
                    TeamInfoSection(newTeamViewModel)
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column {
                            Text(
                                "// SELECT_OPERATORS",
                                color = EndfieldPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                "OPERATORS (4 REQUIRED)",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(EndfieldPurple)
                                    .padding(top = 4.dp)
                            )
                        }
                    }
                }

                items(4) { index ->
                    OperatorSelectorCard(
                        index = index,
                        viewModel = newTeamViewModel
                    )
                }

                if (newTeamViewModel.selectedOperators.size == 4 && newTeamViewModel.selectedOperators.all { it != null }) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column {
                                Text(
                                    "// SELECT_WEAPONS",
                                    color = EndfieldCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    "WEAPONS",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(EndfieldCyan)
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    items(4) { index ->
                        WeaponSelectorCard(
                            index = index,
                            viewModel = newTeamViewModel
                        )
                    }
                }

                if (newTeamViewModel.selectedWeapons.size == 4 && newTeamViewModel.selectedWeapons.all { it != null }) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column {
                                Text(
                                    "// SELECT_GEAR",
                                    color = EndfieldYellow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    "GEAR FOR EACH OPERATOR",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(EndfieldYellow)
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    items(4) { operatorIndex ->
                        GearSelectorSection(
                            operatorIndex = operatorIndex,
                            viewModel = newTeamViewModel
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            if (newTeamViewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = EndfieldPurple)
                        Text(
                            "LOADING_DATA...",
                            color = EndfieldPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }

    if (newTeamViewModel.showingOperatorDialogFor != null) {
        OperatorSelectionDialog(
            viewModel = newTeamViewModel,
            onOperatorSelected = { operatorItem ->
                newTeamViewModel.selectOperator(
                    newTeamViewModel.showingOperatorDialogFor!!,
                    operatorItem.operator
                )
                newTeamViewModel.showingOperatorDialogFor = null
            },
            onDismiss = { newTeamViewModel.showingOperatorDialogFor = null }
        )
    }

    if (newTeamViewModel.showingWeaponDialogFor != null) {
        WeaponSelectionDialog(
            viewModel = newTeamViewModel,
            onWeaponSelected = { weaponItem ->
                newTeamViewModel.selectWeapon(
                    newTeamViewModel.showingWeaponDialogFor!!,
                    weaponItem.weapon
                )
                newTeamViewModel.showingWeaponDialogFor = null
            },
            onDismiss = { newTeamViewModel.showingWeaponDialogFor = null }
        )
    }

    if (newTeamViewModel.showingGearDialogFor != null) {
        val (operatorIndex, gearType) = newTeamViewModel.showingGearDialogFor!!
        GearSelectionDialog(
            gearType = gearType,
            viewModel = newTeamViewModel,
            onGearSelected = { gearItem ->
                newTeamViewModel.selectGear(operatorIndex, gearType, gearItem.gear)
                newTeamViewModel.showingGearDialogFor = null
            },
            onDismiss = { newTeamViewModel.showingGearDialogFor = null }
        )
    }

    if (showSuccessDialog) {
        Dialog(onDismissRequest = { showSuccessDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = TechSurface
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        "// SUCCESS",
                        color = EndfieldPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "TEAM CREATED",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(EndfieldPurple)
                            .padding(top = 4.dp)
                    )
                    Text(
                        "YOUR_TEAM_HAS_BEEN_CREATED_SUCCESSFULLY",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EndfieldPurple)
                            .clickable {
                                showSuccessDialog = false
                                onCreateSuccess()
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "CONTINUE",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeamInfoSection(viewModel: NewTeamViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TechSurface),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column {
                    Text(
                        "// TEAM_INFORMATION",
                        color = EndfieldPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "TEAM DETAILS",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(EndfieldPurple)
                            .padding(top = 4.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column {
                        Text(
                            "TEAM_NAME",
                            color = EndfieldPurple,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        TextField(
                            value = viewModel.teamName,
                            onValueChange = { viewModel.teamName = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, TechBorder)
                                .background(Color.Black),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Black,
                                unfocusedContainerColor = Color.Black,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = EndfieldPurple
                            ),
                            placeholder = {
                                Text(
                                    "ENTER_TEAM_NAME",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            },
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 14.sp,
                                color = Color.White
                            ),
                            maxLines = 1
                        )
                    }

                    Column {
                        Text(
                            "DESCRIPTION",
                            color = EndfieldPurple,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        TextField(
                            value = viewModel.description,
                            onValueChange = { viewModel.description = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .border(1.dp, TechBorder)
                                .background(Color.Black),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Black,
                                unfocusedContainerColor = Color.Black,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = EndfieldPurple
                            ),
                            placeholder = {
                                Text(
                                    "ENTER_TEAM_DESCRIPTION",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            },
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OperatorSelectorCard(index: Int, viewModel: NewTeamViewModel) {
    val operator = viewModel.selectedOperators.getOrNull(index)
    val backgroundColor = if (operator != null) EndfieldPurple.copy(alpha = 0.15f) else TechSurface
    val borderColor = if (operator != null) EndfieldPurple else TechBorder

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor)
            .clickable { viewModel.showingOperatorDialogFor = index },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "SLOT_${index + 1}",
                        color = EndfieldPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        operator?.name?.uppercase() ?: "SELECT_OPERATOR",
                        color = if (operator != null) Color.White else Color.Gray,
                        fontSize = 18.sp,
                        fontWeight = if (operator != null) FontWeight.Black else FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (operator != null) {
                        Text(
                            "${operator.operatorClass} • ${operator.weaponType}",
                            color = EndfieldPurple.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                if (operator != null) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "SELECTED",
                            color = EndfieldPurple,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
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
    val backgroundColor = if (weapon != null) EndfieldCyan.copy(alpha = 0.15f) else TechSurface
    val borderColor = if (weapon != null) EndfieldCyan else TechBorder

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor)
            .clickable(enabled = isSelectable) {
                if (isSelectable) viewModel.showingWeaponDialogFor = index
            },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "WEAPON_FOR",
                            color = EndfieldCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            operator?.name?.uppercase() ?: "OPERATOR_${index + 1}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    if (weapon != null) {
                        Box(
                            modifier = Modifier
                                .background(Color.Black)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "SELECTED",
                                color = EndfieldCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    weapon?.name?.uppercase() ?: "SELECT_WEAPON",
                    color = if (weapon != null) Color.White else Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = if (weapon != null) FontWeight.Black else FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                if (weapon != null) {
                    Text(
                        weapon.weaponType,
                        color = EndfieldCyan.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                } else if (operator != null) {
                    Text(
                        "REQUIRED_TYPE: ${operator.weaponType}",
                        color = EndfieldCyan.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
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
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        "// GEAR_FOR",
                        color = EndfieldYellow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        operator?.name?.uppercase() ?: "OPERATOR_${operatorIndex + 1}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(EndfieldYellow)
                            .padding(top = 4.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GearSelectorRow(
                        gearType = GearType.ARMOR,
                        selectedGear = viewModel.selectedGear[operatorIndex]?.get(GearType.ARMOR),
                        onClick = {
                            viewModel.showingGearDialogFor = Pair(operatorIndex, GearType.ARMOR)
                        }
                    )

                    GearSelectorRow(
                        gearType = GearType.GLOVES,
                        selectedGear = viewModel.selectedGear[operatorIndex]?.get(GearType.GLOVES),
                        onClick = {
                            viewModel.showingGearDialogFor = Pair(operatorIndex, GearType.GLOVES)
                        }
                    )

                    GearSelectorRow(
                        gearType = GearType.KIT1,
                        selectedGear = viewModel.selectedGear[operatorIndex]?.get(GearType.KIT1),
                        onClick = {
                            viewModel.showingGearDialogFor = Pair(operatorIndex, GearType.KIT1)
                        }
                    )

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
    }
}

@Composable
fun GearSelectorRow(
    gearType: GearType,
    selectedGear: Gear?,
    onClick: () -> Unit
) {
    val backgroundColor =
        if (selectedGear != null) EndfieldYellow.copy(alpha = 0.15f) else Color.Transparent
    val borderColor = if (selectedGear != null) EndfieldYellow else TechBorder

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor)
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    gearType.name,
                    color = if (selectedGear != null) EndfieldYellow else Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    selectedGear?.name?.uppercase() ?: "SELECT_${gearType.name}",
                    color = if (selectedGear != null) Color.White else Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = if (selectedGear != null) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (selectedGear != null) {
                Box(
                    modifier = Modifier
                        .background(Color.Black)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "SELECTED",
                        color = EndfieldYellow,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OperatorSelectionDialog(
    viewModel: NewTeamViewModel,
    onOperatorSelected: (SelectionItem.OperatorItem) -> Unit,
    onDismiss: () -> Unit
) {
    SelectionDialog(
        title = "SELECT_OPERATOR",
        items = viewModel.availableOperatorsForSelection(),
        itemName = { it.operator.name.uppercase() },
        itemSubtitle = { "${it.operator.operatorClass} • ${it.operator.weaponType}" },
        accentColor = EndfieldPurple,
        onItemSelected = onOperatorSelected,
        onDismiss = onDismiss
    )
}

@Composable
fun WeaponSelectionDialog(
    viewModel: NewTeamViewModel,
    onWeaponSelected: (SelectionItem.WeaponItem) -> Unit,
    onDismiss: () -> Unit
) {
    SelectionDialog(
        title = "SELECT_WEAPON",
        items = viewModel.availableWeaponsForSelection(viewModel.showingWeaponDialogFor!!),
        itemName = { it.weapon.name.uppercase() },
        itemSubtitle = { it.weapon.weaponType },
        accentColor = EndfieldCyan,
        onItemSelected = onWeaponSelected,
        onDismiss = onDismiss
    )
}

@Composable
fun GearSelectionDialog(
    gearType: GearType,
    viewModel: NewTeamViewModel,
    onGearSelected: (SelectionItem.GearItem) -> Unit,
    onDismiss: () -> Unit
) {
    SelectionDialog(
        title = "SELECT_${gearType.name}",
        items = viewModel.availableGearForSelection(gearType),
        itemName = { it.gear.name.uppercase() },
        itemSubtitle = { "${it.gear.gearSet} • DEF: ${it.gear.def}" },
        accentColor = EndfieldYellow,
        onItemSelected = onGearSelected,
        onDismiss = onDismiss
    )
}

@Composable
fun <T> SelectionDialog(
    title: String,
    items: List<T>,
    itemName: (T) -> String,
    itemSubtitle: (T) -> String? = { null },
    accentColor: Color,
    onItemSelected: (T) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(24.dp),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = TechSurface
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            "// $title",
                            color = accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            title.replace("_", " "),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(accentColor)
                                .padding(top = 4.dp)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(items.size) { index ->
                        val item = items[index]
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onItemSelected(item) }
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text(
                                        itemName(item),
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    itemSubtitle(item)?.let { subtitle ->
                                        Text(
                                            subtitle,
                                            color = accentColor.copy(alpha = 0.8f),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }

                            if (index < items.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(TechBorder)
                                        .padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .clickable { onDismiss() }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "CANCEL",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}