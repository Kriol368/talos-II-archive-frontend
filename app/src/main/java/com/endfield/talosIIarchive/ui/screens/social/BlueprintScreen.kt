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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.endfield.talosIIarchive.domain.models.Blueprint
import com.endfield.talosIIarchive.ui.theme.EndfieldGreen
import com.endfield.talosIIarchive.ui.theme.TechSurface
import com.endfield.talosIIarchive.ui.viewmodel.BlueprintViewModel

@Composable
fun BlueprintScreen(blueprintViewModel: BlueprintViewModel) {
    val context = LocalContext.current
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