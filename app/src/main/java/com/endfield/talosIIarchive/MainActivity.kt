package com.endfield.talosIIarchive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.endfield.talosIIarchive.domain.repositoty.BlueprintRepositoryImpl
import com.endfield.talosIIarchive.domain.repositoty.GearRepositoryImpl
import com.endfield.talosIIarchive.domain.repositoty.OperatorRepositoryImpl
import com.endfield.talosIIarchive.domain.repositoty.TeamRepositoryImpl
import com.endfield.talosIIarchive.domain.repositoty.WeaponRepositoryImpl
import com.endfield.talosIIarchive.ui.screens.home.HomeScreen
import com.endfield.talosIIarchive.ui.screens.social.NewTeamViewModel
import com.endfield.talosIIarchive.ui.screens.social.SocialScreen
import com.endfield.talosIIarchive.ui.screens.wiki.WikiScreen
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldOrange
import com.endfield.talosIIarchive.ui.theme.EndfieldYellow
import com.endfield.talosIIarchive.ui.theme.TalosIIarchivefrontendTheme
import com.endfield.talosIIarchive.ui.theme.TechSurface
import com.endfield.talosIIarchive.ui.viewmodel.BlueprintViewModel
import com.endfield.talosIIarchive.ui.viewmodel.BlueprintViewModelFactory
import com.endfield.talosIIarchive.ui.viewmodel.GearViewModel
import com.endfield.talosIIarchive.ui.viewmodel.GearViewModelFactory
import com.endfield.talosIIarchive.ui.viewmodel.NewTeamViewModelFactory
import com.endfield.talosIIarchive.ui.viewmodel.OperatorViewModel
import com.endfield.talosIIarchive.ui.viewmodel.OperatorViewModelFactory
import com.endfield.talosIIarchive.ui.viewmodel.TeamViewModel
import com.endfield.talosIIarchive.ui.viewmodel.TeamViewModelFactory
import com.endfield.talosIIarchive.ui.viewmodel.WeaponViewModel
import com.endfield.talosIIarchive.ui.viewmodel.WeaponViewModelFactory
import kotlinx.coroutines.launch

sealed class Screen(
    val title: String, val iconResId: Int, val route: String
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
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val operatorRepository = OperatorRepositoryImpl()
                    val weaponRepository = WeaponRepositoryImpl()
                    val gearRepository = GearRepositoryImpl()
                    val blueprintRepository = BlueprintRepositoryImpl()
                    val teamRepository = TeamRepositoryImpl()

                    val operatorViewModel: OperatorViewModel = viewModel(
                        factory = OperatorViewModelFactory(operatorRepository)
                    )

                    val weaponViewModel: WeaponViewModel = viewModel(
                        factory = WeaponViewModelFactory(weaponRepository)
                    )

                    val gearViewModel: GearViewModel = viewModel(
                        factory = GearViewModelFactory(gearRepository)
                    )

                    val blueprintViewModel: BlueprintViewModel = viewModel(
                        factory = BlueprintViewModelFactory(blueprintRepository)
                    )

                    val teamViewModel: TeamViewModel = viewModel(
                        factory = TeamViewModelFactory(teamRepository)
                    )

                    val newTeamViewModel: NewTeamViewModel = viewModel(
                        factory = NewTeamViewModelFactory(
                            operatorRepository,
                            weaponRepository,
                            gearRepository
                        )
                    )


                    App(
                        operatorViewModel,
                        weaponViewModel,
                        gearViewModel,
                        blueprintViewModel,
                        teamViewModel,
                        newTeamViewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    operatorViewModel: OperatorViewModel,
    weaponViewModel: WeaponViewModel,
    gearViewModel: GearViewModel,
    blueprintViewModel: BlueprintViewModel,
    teamViewModel: TeamViewModel,
    newTeamViewModel: NewTeamViewModel
) {
    val screens = listOf(
        Screen.Home,
        Screen.Operators,
        Screen.Weapons,
    )

    val pagerState = rememberPagerState(pageCount = { screens.size })
    val coroutineScope = rememberCoroutineScope()

    // Obtener el tamaño de la barra de navegación del sistema
    val systemNavigationBarHeight =
        WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        bottomBar = {
            Column {
                // Barra de navegación principal con safe area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TechSurface)
                        .padding(bottom = systemNavigationBarHeight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        screens.forEachIndexed { index, screen ->
                            val isSelected = pagerState.currentPage == index
                            val accentColor = when (index) {
                                0 -> EndfieldYellow
                                1 -> EndfieldOrange
                                2 -> EndfieldCyan
                                else -> EndfieldOrange
                            }

                            NavigationBarItemEnhanced(
                                screen = screen,
                                isSelected = isSelected,
                                accentColor = accentColor,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
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
                0 -> HomeScreen(
                    onWikiClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    onSocialClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    }
                )

                1 -> WikiScreen(operatorViewModel, weaponViewModel, gearViewModel)
                2 -> SocialScreen(blueprintViewModel, teamViewModel, newTeamViewModel)
                else -> HomeScreen(
                    onWikiClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    onSocialClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NavigationBarItemEnhanced(
    screen: Screen,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            // Icono grande
            Box(
                modifier = Modifier
                    .size(44.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = screen.iconResId),
                    contentDescription = screen.title,
                    modifier = Modifier.size(26.dp),
                    tint = if (isSelected) accentColor else Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Texto del ítem
            Text(
                text = screen.title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = if (isSelected) accentColor else Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Indicador de selección (punto debajo del texto)
            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(accentColor, shape = CircleShape)
                )
            } else {
                // ID de módulo cuando no está seleccionado
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = when (screen.title) {
                        "Home" -> "00"
                        "Operators" -> "01"
                        "Weapons" -> "02"
                        else -> "00"
                    },
                    color = Color.Gray.copy(alpha = 0.4f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}