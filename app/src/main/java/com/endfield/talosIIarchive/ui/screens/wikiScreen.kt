package com.endfield.talosIIarchive.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.endfield.talosIIarchive.domain.models.Weapon
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldOrange
import com.endfield.talosIIarchive.ui.theme.EndfieldYellow
import com.endfield.talosIIarchive.ui.theme.TechBackground
import com.endfield.talosIIarchive.ui.theme.TechBlack
import com.endfield.talosIIarchive.ui.theme.TechBorder
import com.endfield.talosIIarchive.ui.theme.TechSurface
import com.endfield.talosIIarchive.ui.viewmodel.OperatorViewModel

enum class WikiCategory {
    OPERATORS, WEAPONS, GEAR
}

// --- PANTALLA PRINCIPAL ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WikiScreen(viewModel: OperatorViewModel) {
    // Solo mantenemos la categoría local, el operador detallado viene del VM
    var selectedCategory by remember { mutableStateOf<WikiCategory?>(null) }

    Surface(modifier = Modifier.fillMaxSize(), color = TechBackground) {
        when {
            // 1. PRIORIDAD MÁXIMA: Si se está cargando el detalle del servidor
            viewModel.isDetailLoading -> {
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

            // 2. Si ya tenemos el Operador completo con sus stats
            viewModel.selectedOperatorFull != null -> {
                OperatorDetailScreen(viewModel.selectedOperatorFull!!) {
                    viewModel.clearSelectedOperator() // Función en VM para poner a null
                }
            }

            // 3. Si el usuario ha elegido una categoría (Lista de Ops o Armas)
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
                        },
                        navigationIcon = {
                            IconButton(onClick = { selectedCategory = null }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = EndfieldOrange)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = TechSurface,
                            titleContentColor = Color.White
                        )
                    )

                    when (selectedCategory) {
                        WikiCategory.OPERATORS -> {
                            OperatorList(viewModel) { op ->
                                // Llamamos a la petición por ID para traer stats reales
                                viewModel.fetchOperatorDetails(op.id)
                            }
                        }
                        WikiCategory.WEAPONS -> WeaponListScreen(viewModel)
                        WikiCategory.GEAR -> GearListPlaceholder()
                        else -> {}
                    }
                }
            }

            // 4. ESTADO INICIAL: Menú de 3 Botones
            else -> {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text(
                        text = "// ARCHIVE_SYSTEM_V.2.0",
                        color = EndfieldOrange,
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
                    WikiMenuButton("03", "GEAR", Color.White, Modifier.weight(1f)) {
                        selectedCategory = WikiCategory.GEAR
                    }
                }
            }
        }
    }
}


@Composable
fun GearListPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("GEAR_DATABASE_OFFLINE", color = Color.Red, fontWeight = FontWeight.Bold)
    }
}
// --- LISTA DE OPERADORES ACTUALIZADA ---
@Composable
fun OperatorList(viewModel: OperatorViewModel, onOperatorClick: (Operator) -> Unit) {
    LaunchedEffect(Unit) {
        viewModel.fetchOperators()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = EndfieldOrange)
        } else if (viewModel.operators.isEmpty()) {
            Text("DATA_NOT_FOUND", color = Color.Red, modifier = Modifier.align(Alignment.Center))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.operators) { operator ->
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
            .aspectRatio(0.7f) // Proporción vertical para ver el dibujo completo
            .border(0.5.dp, TechBorder)
            .background(TechSurface)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = if (operator.imageUrl.startsWith("http")) operator.imageUrl
            else "http://10.0.2.2:8080${operator.imageUrl}",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().alpha(0.85f)
        )

        // Gradiente inferior para legibilidad
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.8f)), startY = 400f)
        ))

        Column(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)) {
            Text(operator.rarity, color = rarityColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                operator.name.uppercase(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 20.sp
            )
        }

        // ID Real de la base de datos
        Text(
            "№ ${operator.id}",
            color = Color.White.copy(0.3f),
            fontSize = 9.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
        )
    }
}
@Composable
fun WikiMenuButton(number: String, title: String, accentColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, TechBorder)
            .background(TechSurface)
            .clickable { onClick() }
    ) {
        // Decoración lateral
        Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(accentColor))

        Column(modifier = Modifier.padding(24.dp).align(Alignment.CenterStart)) {
            Text(text = "ID.$number", color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(
                text = title,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )
        }

        // Elemento decorativo tipo "puntero" en la esquina
        Text(
            text = "ACCESS >",
            color = Color.DarkGray,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            fontSize = 10.sp
        )
    }
}

// --- FICHA TÉCNICA (DETALLES FUNCIONALES) ---
@Composable
fun OperatorDetailScreen(operator: Operator, onBack: () -> Unit) {
    // Estado para saber qué pestaña de información estamos viendo
    var activeTab by remember { mutableStateOf("SKILLS") }
    val scrollState = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize(), color = TechBlack) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {

                // --- 1. HEADER (Splash Art + Nombre) ---
                Box(modifier = Modifier.height(380.dp).fillMaxWidth()) {
                    AsyncImage(
                        model = "http://10.0.2.2:8080${operator.imageUrl}",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, TechBlack))))

                    IconButton(onClick = onBack, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).statusBarsPadding()) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                    }

                    Column(Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                        Text("REC // PERSONAL_FILE", color = EndfieldYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(operator.name.uppercase(), fontSize = 46.sp, fontWeight = FontWeight.Black, color = Color.White)
                        DataTag("CLASS", operator.operatorClass, EndfieldYellow, Color.Black)
                    }
                }

                // --- 2. STATS FIJOS ---
                Row(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FunctionalStatItem("STR", operator.strength)
                    FunctionalStatItem("AGI", operator.agility)
                    FunctionalStatItem("INT", operator.intellect)
                    FunctionalStatItem("WILL", operator.will)
                }

                // --- 3. SELECTOR DE BOTONES (TABS) ---
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EndfieldTabButton("Skills", "MOD_01", activeTab == "SKILLS", Modifier.weight(1f)) {
                        activeTab = "SKILLS"
                    }
                    EndfieldTabButton("Talents", "MOD_02", activeTab == "TALENTS", Modifier.weight(1f)) {
                        activeTab = "TALENTS"
                    }
                    EndfieldTabButton("Resonance", "MOD_03", activeTab == "RESONANCE", Modifier.weight(1f)) {
                        activeTab = "RESONANCE"
                    }
                }

                // --- 4. PANEL DE INFORMACIÓN DINÁMICO ---
                Box(modifier = Modifier.padding(24.dp).fillMaxWidth().heightIn(min = 400.dp)) {
                    when (activeTab) {
                        "SKILLS" -> Column {
                            SkillBlock(operator.basicAttack, "BASIC_ATK", operator.basicAttackDescription)
                            SkillBlock(operator.battleSkill, "BATTLE_SKILL", operator.battleSkillDescription)
                            SkillBlock(operator.ultimate, "ULTIMATE", operator.ultimateDescription)
                        }
                        "TALENTS" -> Column {
                            TalentBlock("COMBAT_TALENT_01", operator.combatTalent1)
                            TalentBlock("COMBAT_TALENT_02", operator.combatTalent2)
                            TalentBlock("BASE_LOGISTICS", operator.baseTalent1)
                        }
                        "RESONANCE" -> Column {
                            PotentialRow("P1", operator.p1, operator.p1Effect)
                            PotentialRow("P2", operator.p2, operator.p2Effect)
                            PotentialRow("P3", operator.p3, operator.p3Effect)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }


        }
    }
}


@Composable
fun EndfieldTabButton(
    label: String,
    subLabel: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) EndfieldYellow else TechSurface
    val textColor = if (isSelected) Color.Black else Color.White
    val borderColor = if (isSelected) EndfieldYellow else TechBorder

    Box(
        modifier = modifier
            .height(50.dp)
            .border(1.dp, borderColor)
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = subLabel,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.Black.copy(0.6f) else EndfieldYellow
            )
            Text(
                text = label.uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = textColor,
                letterSpacing = 1.sp
            )
        }

        // Elemento decorativo en la esquina si está seleccionado
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(10.dp)
                    .background(Color.Black)
            )
        }
    }
}
// --- COMPONENTES AUXILIARES CON LA ESTÉTICA DE LA IMAGEN ---

@Composable
fun DataTag(label: String, value: String, bgColor: Color, textColor: Color) {
    Row(modifier = Modifier.border(1.dp, TechBorder)) {
        Box(modifier = Modifier.background(bgColor).padding(horizontal = 6.dp, vertical = 2.dp)) {
            Text(label, color = textColor, fontSize = 9.sp, fontWeight = FontWeight.Black)
        }
        Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)) {
            Text(value.uppercase(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FunctionalStatItem(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value.toString(), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
        // Línea amarilla decorativa debajo del stat
        Box(Modifier.size(15.dp, 2.dp).background(EndfieldYellow))
    }
}







@Composable
fun SkillBlock(name: String?, type: String, desc: String?) {
    if (name == null) return
    Column(Modifier.padding(bottom = 20.dp)) {
        Text(type, color = EndfieldCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(name.uppercase(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
        Text(desc ?: "", color = Color.LightGray, fontSize = 12.sp, lineHeight = 16.sp)
    }
}

@Composable
fun TalentBlock(label: String, desc: String?) {
    if (desc.isNullOrBlank()) return
    Column(Modifier.padding(bottom = 12.dp).border(1.dp, TechBorder).padding(12.dp)) {
        Text(label, color = EndfieldOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(desc, color = Color.White, fontSize = 12.sp)
    }
}

@Composable
fun PotentialRow(level: String, name: String?, effect: String?) {
    if (name == null) return
    Row(Modifier.padding(vertical = 6.dp)) {
        Text(level, color = EndfieldCyan, fontWeight = FontWeight.Bold, modifier = Modifier.width(35.dp))
        Column {
            Text(name.uppercase(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(effect ?: "", color = Color.Gray, fontSize = 11.sp)
        }
    }
}


@Composable
fun WeaponListScreen(viewModel: OperatorViewModel) {
    LaunchedEffect(Unit) {
        viewModel.fetchWeapons()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (viewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.Cyan
            )
        } else if (viewModel.weapons.isEmpty()) {
            // 👈 AÑADE ESTO: Si no hay armas, mostramos este texto
            Text(
                "No se encontraron armas en la DB",
                modifier = Modifier.align(Alignment.Center),
                color = Color.Gray
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.weapons) { weapon ->
                    WeaponCard(weapon)
                }
            }
        }
    }
}
@Composable
fun WeaponCard(weapon: Weapon) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFF1E1E1E) // Gris oscuro
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // IMAGEN (Usamos la URL directamente porque el backend ya la manda completa)
            AsyncImage(
                model = weapon.imageUrl,
                contentDescription = weapon.name,
                modifier = Modifier
                    .size(70.dp)
                    .background(Color(0xFF2D2D2D), RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = weapon.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "${weapon.weaponType} | ATK: ${weapon.baseAtk}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Cyan
                )
                // Estrellas doradas
                Text(
                    text = weapon.rarity,
                    color = Color(0xFFFFD700),
                    style = MaterialTheme.typography.bodyMedium
                )

                // Solo mostramos la pasiva si el servidor la envía
                if (!weapon.passive.isNullOrBlank()) {
                    Text(
                        text = weapon.passive,
                        color = Color.LightGray,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2
                    )
                }
            }
        }
    }
}