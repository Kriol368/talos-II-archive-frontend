package com.endfield.talosIIarchive.ui.screens.wiki


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.endfield.talosIIarchive.ui.viewmodel.OperatorViewModel

enum class WikiCategory {
    OPERATORS, WEAPONS, GEAR
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WikiScreen(viewModel: OperatorViewModel) {
    var selectedCategory by remember { mutableStateOf<WikiCategory?>(null) }

    Surface(modifier = Modifier.fillMaxSize(), color = TechBackground) {
        when {
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

            viewModel.selectedOperatorFull != null -> {
                OperatorDetailScreen(viewModel.selectedOperatorFull!!) {
                    viewModel.clearSelectedOperator()
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
                            OperatorList(viewModel) { op ->
                                viewModel.fetchOperatorDetails(op.id)
                            }
                        }

                        WikiCategory.WEAPONS -> WeaponListScreen()
                        WikiCategory.GEAR -> GearListPlaceholder()
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
fun WikiMenuButton(
    number: String,
    title: String,
    accentColor: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, TechBorder)
            .background(TechSurface)
            .clickable { onClick() }
    ) {
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

@Composable
fun DataTag(label: String, value: String, bgColor: Color, textColor: Color) {
    Row(modifier = Modifier.border(1.dp, TechBorder)) {
        Box(
            modifier = Modifier
                .background(bgColor)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(label, color = textColor, fontSize = 9.sp, fontWeight = FontWeight.Black)
        }
        Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)) {
            Text(
                value.uppercase(),
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FunctionalStatItem(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value.toString(), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Box(
            Modifier
                .size(15.dp, 2.dp)
                .background(EndfieldYellow)
        )
    }
}


@Composable
fun TalentBlock(label: String, desc: String?) {
    if (desc.isNullOrBlank()) return
    Column(
        Modifier
            .padding(bottom = 12.dp)
            .border(1.dp, TechBorder)
            .padding(12.dp)
    ) {
        Text(label, color = EndfieldOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(desc, color = Color.White, fontSize = 12.sp)
    }
}

@Composable
fun PotentialRow(level: String, name: String?, effect: String?) {
    if (name == null) return
    Row(Modifier.padding(vertical = 6.dp)) {
        Text(
            level,
            color = EndfieldCyan,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(35.dp)
        )
        Column {
            Text(
                name.uppercase(),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(effect ?: "", color = Color.Gray, fontSize = 11.sp)
        }
    }
}
