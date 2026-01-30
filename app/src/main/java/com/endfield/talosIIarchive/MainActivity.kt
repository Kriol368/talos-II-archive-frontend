package com.endfield.talosIIarchive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.endfield.talosIIarchive.gear.GearScreen
import com.endfield.talosIIarchive.home.HomeScreen
import com.endfield.talosIIarchive.operator.OperatorsScreen
import com.endfield.talosIIarchive.weapon.WeaponsScreen
import com.endfield.talosIIarchive.ui.theme.TalosIIarchivefrontendTheme
import kotlinx.coroutines.launch

sealed class Screen(val title: String, val icon: ImageVector, val route: String) {
    object Home : Screen("Home", Icons.Default.Home, "home")
    object Operators : Screen("Operators", Icons.Default.Person, "operators")
    object Weapons : Screen("Weapons", Icons.Default.Person, "weapons")
    object Gear : Screen("Gear", Icons.Default.Build, "gear")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TalosIIarchivefrontendTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val screens = listOf(
        Screen.Home,
        Screen.Operators,
        Screen.Weapons,
        Screen.Gear
    )

    val pagerState = rememberPagerState(pageCount = { screens.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar {
                screens.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> HomeScreen()
                1 -> OperatorsScreen()
                2 -> WeaponsScreen()
                3 -> GearScreen()
                else -> HomeScreen()
            }
        }
    }
}