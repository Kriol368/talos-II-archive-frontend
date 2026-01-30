package com.endfield.talosIIarchive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.endfield.talosIIarchive.gear.GearScreen
import com.endfield.talosIIarchive.home.HomeScreen
import com.endfield.talosIIarchive.operators.OperatorsScreen
import com.endfield.talosIIarchive.ui.theme.TalosIIarchivefrontendTheme
import com.endfield.talosIIarchive.weapon.WeaponsScreen
import kotlinx.coroutines.launch

sealed class Screen(
    val title: String,
    val iconResId: Int,
    val route: String
) {
    object Home : Screen("Home", R.drawable.home, "home")
    object Operators : Screen("Operators", R.drawable.operator, "operators")
    object Weapons : Screen("Weapons", R.drawable.weapon, "weapons")
    object Gear : Screen("Gear", R.drawable.gear, "gear")
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
                            Image(
                                painter = painterResource(id = screen.iconResId),
                                contentDescription = screen.title,
                                modifier = Modifier.size(48.dp),
                            )
                        },
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