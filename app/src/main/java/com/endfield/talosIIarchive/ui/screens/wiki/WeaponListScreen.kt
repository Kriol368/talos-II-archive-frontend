package com.endfield.talosIIarchive.ui.screens.wiki

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.endfield.talosIIarchive.domain.models.Weapon
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldOrange
import com.endfield.talosIIarchive.ui.theme.TechBorder
import com.endfield.talosIIarchive.ui.theme.TechSurface
import com.endfield.talosIIarchive.ui.viewmodel.WeaponViewModel

@Composable
fun WeaponListScreen(
    weaponViewModel: WeaponViewModel,
    onWeaponClick: (Weapon) -> Unit
) {
    LaunchedEffect(Unit) {
        if (weaponViewModel.weapons.isEmpty()) {
            weaponViewModel.fetchWeapons()
        }
    }

    val weapons = weaponViewModel.weapons
    val configuration = LocalConfiguration.current

    val columnCount = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 2


    Box(modifier = Modifier.fillMaxSize()) {
        if (weaponViewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = EndfieldOrange
            )
        } else if (weaponViewModel.weapons.isEmpty()) {
            Text("DATA_NOT_FOUND", color = Color.Red, modifier = Modifier.align(Alignment.Center))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columnCount),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(weaponViewModel.weapons) { weapon ->
                    WeaponrGridItem(weapon) { onWeaponClick(weapon) }

                }
            }
        }
    }
}


@Composable
fun WeaponrGridItem(weapon: Weapon,onClick:()-> Unit){
    val rarityColor = if (weapon.rarity.contains("6")) EndfieldOrange else EndfieldCyan

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .border(0.5.dp, TechBorder)
            .background(TechSurface)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = if (weapon.imageUrl.startsWith("http")) weapon.imageUrl
            else "http://158.179.216.16:8080${weapon.imageUrl}",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.85f)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(0.8f)),
                        startY = 400f
                    )
                )
        )

        Column(modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(12.dp)) {
            Text(
                weapon.rarity,
                color = rarityColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                weapon.name.uppercase(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 20.sp
            )
        }

        Text(
            "№ ${weapon.id}",
            color = Color.White.copy(0.3f),
            fontSize = 9.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
    }
}


