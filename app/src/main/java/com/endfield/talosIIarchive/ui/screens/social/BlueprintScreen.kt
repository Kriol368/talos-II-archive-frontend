package com.endfield.talosIIarchive.ui.screens.social

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.endfield.talosIIarchive.domain.models.Blueprint
import com.endfield.talosIIarchive.ui.theme.*
import com.endfield.talosIIarchive.ui.viewmodel.BlueprintViewModel
import kotlinx.coroutines.launch

@Composable
fun BlueprintScreen(blueprintViewModel: BlueprintViewModel) {
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        blueprintViewModel.fetchBlueprints()
    }

    Box(modifier = Modifier.fillMaxSize().background(TechBackground)) {
        when {
            blueprintViewModel.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = EndfieldGreen)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "LOADING_BLUEPRINT_FILES...",
                        color = EndfieldGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            blueprintViewModel.errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "SYSTEM_ERROR",
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        blueprintViewModel.errorMessage ?: "UNKNOWN_ERROR",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            blueprintViewModel.blueprints.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "NO_BLUEPRINTS_FOUND",
                            color = EndfieldGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "CREATE_THE_FIRST_BLUEPRINT_IN_THE_COMMUNITY",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(blueprintViewModel.blueprints) { blueprint ->
                        BlueprintCard(
                            blueprint = blueprint,
                            onCopyClick = { codeHash ->
                                copyToClipboard(context, codeHash, blueprint.title)
                            }
                        )
                    }
                }
            }
        }

        // FAB con estilo del tema
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EndfieldGreen)
                    .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                    .clickable { showCreateDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Create Blueprint",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateBlueprintDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, description, codeHash ->
                coroutineScope.launch {
                    createBlueprint(
                        context = context,
                        viewModel = blueprintViewModel,
                        title = title,
                        description = description,
                        codeHash = codeHash
                    )
                    showCreateDialog = false
                }
            }
        )
    }
}

@Composable
fun CreateBlueprintDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var codeHash by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Column {
                    Text(
                        "// NEW_BLUEPRINT_ENTRY",
                        color = EndfieldGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "CREATE BLUEPRINT",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(EndfieldGreen)
                            .padding(top = 4.dp)
                    )
                }

                // Campos del formulario
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column {
                        Text(
                            "BLUEPRINT_TITLE",
                            color = EndfieldGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        TextField(
                            value = title,
                            onValueChange = { title = it },
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
                                cursorColor = EndfieldGreen
                            ),
                            placeholder = {
                                Text(
                                    "ENTER_BLUEPRINT_NAME",
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
                            color = EndfieldGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        TextField(
                            value = description,
                            onValueChange = { description = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .border(1.dp, TechBorder)
                                .background(Color.Black),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Black,
                                unfocusedContainerColor = Color.Black,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = EndfieldGreen
                            ),
                            placeholder = {
                                Text(
                                    "ENTER_BLUEPRINT_DESCRIPTION",
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

                    Column {
                        Text(
                            "CODE_HASH",
                            color = EndfieldGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        TextField(
                            value = codeHash,
                            onValueChange = { codeHash = it },
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
                                cursorColor = EndfieldGreen
                            ),
                            placeholder = {
                                Text(
                                    "ENTER_CODE_HASH",
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
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .border(1.dp, TechBorder)
                            .background(Color.Black)
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            "CANCEL",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .clickable {
                                if (title.isNotBlank() && description.isNotBlank() && codeHash.isNotBlank()) {
                                    onCreate(title, description, codeHash)
                                }
                            }
                            .background(
                                if (title.isNotBlank() && description.isNotBlank() && codeHash.isNotBlank())
                                    EndfieldGreen
                                else EndfieldGreen.copy(alpha = 0.5f)
                            )
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            "CREATE",
                            color = Color.Black,
                            fontSize = 12.sp,
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
fun BlueprintCard(
    blueprint: Blueprint,
    onCopyClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TechBorder)
            .clickable { onCopyClick(blueprint.codeHash) },
        colors = CardDefaults.cardColors(
            containerColor = TechSurface
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header con ID
            Text(
                text = "BP_${blueprint.id.toString().padStart(3, '0')}",
                color = EndfieldGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Título
            Text(
                text = blueprint.title.uppercase(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Descripción
            Text(
                text = blueprint.description,
                color = Color.LightGray,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hash del código
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        "CODE_HASH",
                        color = EndfieldGreen,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        blueprint.codeHash,
                        color = Color.Gray,
                        fontSize = 9.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, codeHash: String, title: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Blueprint Code", codeHash)
    clipboard.setPrimaryClip(clip)

    Toast.makeText(
        context,
        "Code for '$title' copied to clipboard!",
        Toast.LENGTH_SHORT
    ).show()
}

private suspend fun createBlueprint(
    context: Context,
    viewModel: BlueprintViewModel,
    title: String,
    description: String,
    codeHash: String
) {
    try {
        val success = viewModel.createBlueprint(title, description, codeHash)
        if (success) {
            Toast.makeText(
                context,
                "Blueprint created successfully!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "Failed to create blueprint",
                Toast.LENGTH_SHORT
            ).show()
        }
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Error creating blueprint: ${e.message}",
            Toast.LENGTH_SHORT
        ).show()
    }
}