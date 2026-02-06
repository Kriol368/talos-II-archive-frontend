package com.endfield.talosIIarchive.ui.screens.social

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.endfield.talosIIarchive.domain.models.Blueprint
import com.endfield.talosIIarchive.ui.theme.EndfieldGreen
import com.endfield.talosIIarchive.ui.theme.TechSurface
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

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            blueprintViewModel.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = EndfieldGreen)
                    Spacer(Modifier.height(16.dp))
                    Text("LOADING_BLUEPRINTS...", color = Color.Gray)
                }
            }

            blueprintViewModel.errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("ERROR", color = Color.Red, fontWeight = FontWeight.Bold)
                    Text(blueprintViewModel.errorMessage ?: "Unknown error", color = Color.Gray)
                }
            }

            blueprintViewModel.blueprints.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("NO_BLUEPRINTS_FOUND", color = Color.Gray)
                    Text(
                        "Create the first blueprint in the community!",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            else -> {
                LazyColumn(
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

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = EndfieldGreen,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Blueprint")
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
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = TechSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "CREATE NEW BLUEPRINT",
                    color = EndfieldGreen,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EndfieldGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = EndfieldGreen,
                        unfocusedLabelColor = Color.Gray
                    ),
                    maxLines = 1
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EndfieldGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = EndfieldGreen,
                        unfocusedLabelColor = Color.Gray
                    ),
                    maxLines = 4
                )

                OutlinedTextField(
                    value = codeHash,
                    onValueChange = { codeHash = it },
                    label = { Text("Code Hash") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EndfieldGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = EndfieldGreen,
                        unfocusedLabelColor = Color.Gray
                    ),
                    maxLines = 1,
                    placeholder = { Text("Enter blueprint code") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank() && description.isNotBlank() && codeHash.isNotBlank()) {
                                onCreate(title, description, codeHash)
                            }
                        },
                        enabled = title.isNotBlank() && description.isNotBlank() && codeHash.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EndfieldGreen,
                            disabledContainerColor = EndfieldGreen.copy(alpha = 0.5f)
                        )
                    ) {
                        Text("CREATE")
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
            .clickable { onCopyClick(blueprint.codeHash) },
        colors = CardDefaults.cardColors(
            containerColor = TechSurface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = blueprint.title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = blueprint.description,
                color = Color.Gray,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
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