package com.endfield.talosIIarchive.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.endfield.talosIIarchive.ui.components.OperatorCard
import com.endfield.talosIIarchive.ui.viewmodel.OperatorViewModel

@Composable
fun wikiScreen(viewModel: OperatorViewModel) {
OperatorListScreen(viewModel)
}
@Composable
fun OperatorListScreen(viewModel: OperatorViewModel) {
    // Se ejecuta una sola vez cuando entra la pantalla
    LaunchedEffect(Unit) {
        viewModel.fetchOperators()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(viewModel.operators) { operator ->
                    OperatorCard(operator = operator)
                }
            }
        }
    }
}