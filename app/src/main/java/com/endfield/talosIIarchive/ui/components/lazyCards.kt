package com.endfield.talosIIarchive.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.endfield.talosIIarchive.domain.models.Operator

@Composable
fun OperatorCard(operator: Operator) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = operator.name, style = MaterialTheme.typography.titleLarge)
                Text(text = "${operator.operatorClass} | ${operator.element}",
                    style = MaterialTheme.typography.bodyMedium)
            }
            // Aquí podrías añadir la rareza (estrellas)
            Text(text = "⭐".repeat(operator.rarity), color = MaterialTheme.colorScheme.primary)
        }
    }
}