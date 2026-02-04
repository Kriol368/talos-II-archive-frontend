package com.endfield.talosIIarchive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.endfield.talosIIarchive.domain.repositoty.OperatorRepositoryImpl
import com.endfield.talosIIarchive.domain.repositoty.WeaponRepositoryImpl
import com.endfield.talosIIarchive.ui.screens.social.SocialScreen
import com.endfield.talosIIarchive.ui.screens.wiki.WikiScreen
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldOrange
import com.endfield.talosIIarchive.ui.theme.TalosIIarchivefrontendTheme
import com.endfield.talosIIarchive.ui.theme.TechBorder
import com.endfield.talosIIarchive.ui.theme.TechSurface
import com.endfield.talosIIarchive.ui.viewmodel.OperatorViewModel
import com.endfield.talosIIarchive.ui.viewmodel.OperatorViewModelFactory
import com.endfield.talosIIarchive.ui.viewmodel.WeaponViewModel
import com.endfield.talosIIarchive.ui.viewmodel.WeaponViewModelFactory
import com.example.arknightsendfield.HomeScreen
import kotlinx.coroutines.launch

sealed class Screen(
    val title: String,
    val iconResId: Int,
    val route: String
) {
    object Home : Screen("Home", R.drawable.home, "home")
    object Operators : Screen("Wiki", R.drawable.operator, "operators")
    object Weapons : Screen("Social", R.drawable.weapon, "weapons")
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
                    // Create repositories
                    val operatorRepository = OperatorRepositoryImpl()
                    val weaponRepository = WeaponRepositoryImpl()

                    // Create ViewModels with their factories
                    val operatorViewModel: OperatorViewModel = viewModel(
                        factory = OperatorViewModelFactory(operatorRepository)
                    )

                    val weaponViewModel: WeaponViewModel = viewModel(
                        factory = WeaponViewModelFactory(weaponRepository)
                    )

                    // Pass both ViewModels to App
                    App(operatorViewModel, weaponViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    operatorViewModel: OperatorViewModel,
    weaponViewModel: WeaponViewModel
) {
    val screens = listOf(
        Screen.Home,
        Screen.Operators,
        Screen.Weapons,
    )

    val pagerState = rememberPagerState(pageCount = { screens.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(TechBorder)
                )

                NavigationBar(
                    containerColor = TechSurface,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(72.dp)
                ) {
                    screens.forEachIndexed { index, screen ->
                        val isSelected = pagerState.currentPage == index
                        val accentColor =
                            if (index == 1) EndfieldOrange else EndfieldCyan

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent,
                                selectedIconColor = accentColor,
                                unselectedIconColor = Color.Gray,
                                selectedTextColor = accentColor,
                                unselectedTextColor = Color.Gray
                            ),
                            icon = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        painter = painterResource(id = screen.iconResId),
                                        contentDescription = screen.title,
                                        modifier = Modifier.size(28.dp),
                                        tint = if (isSelected) accentColor else Color.DarkGray
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        text = screen.title.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                        fontSize = 10.sp,
                                        letterSpacing = 1.sp
                                    )

                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 2.dp)
                                                .width(20.dp)
                                                .height(2.dp)
                                                .background(accentColor)
                                        )
                                    }
                                }
                            },
                            label = { }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) { page ->
            when (page) {
                0 -> HomeScreen()
                1 -> WikiScreen(operatorViewModel, weaponViewModel)
                2 -> SocialScreen()
                else -> HomeScreen()
            }
        }
    }
}