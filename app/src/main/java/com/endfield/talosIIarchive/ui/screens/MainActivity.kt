package com.endfield.talosIIarchive.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.endfield.talosIIarchive.R
import com.endfield.talosIIarchive.domain.repositoty.OperatorRepositoryImpl
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldOrange
import com.endfield.talosIIarchive.ui.theme.TalosIIarchivefrontendTheme
import com.endfield.talosIIarchive.ui.theme.TechBorder
import com.endfield.talosIIarchive.ui.theme.TechSurface
import com.endfield.talosIIarchive.ui.viewmodel.OperatorViewModel
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
                    val repository = OperatorRepositoryImpl()
                    val viewModel: OperatorViewModel = viewModel(
                        factory = ViewModelFactory(repository)
                    )
                    App(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(viewmodel: OperatorViewModel) {
    val screens = listOf(
        Screen.Home,
        Screen.Operators,
        Screen.Weapons,
    )

    val pagerState = rememberPagerState(pageCount = { screens.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            // --- NAVIGATION BAR ESTILO ENDFIELD ---
            Column {
                // Línea decorativa superior (Efecto borde industrial)
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(TechBorder))

                NavigationBar(
                    containerColor = TechSurface,
                    tonalElevation = 0.dp, // Eliminamos la elevación suave
                    modifier = Modifier.height(72.dp)
                ) {
                    screens.forEachIndexed { index, screen ->
                        val isSelected = pagerState.currentPage == index
                        val accentColor = if (index == 1) EndfieldOrange else EndfieldCyan // Wiki en naranja, otros en cian

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            // Eliminamos el fondo de "píldora" de Material 3
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent,
                                selectedIconColor = accentColor,
                                unselectedIconColor = Color.Gray,
                                selectedTextColor = accentColor,
                                unselectedTextColor = Color.Gray
                            ),
                            icon = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    // Icono con tinte dinámico
                                    Icon(
                                        painter = painterResource(id = screen.iconResId),
                                        contentDescription = screen.title,
                                        modifier = Modifier.size(28.dp),
                                        tint = if (isSelected) accentColor else Color.DarkGray
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    // Texto tipo terminal
                                    Text(
                                        text = screen.title.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                        fontSize = 10.sp,
                                        letterSpacing = 1.sp
                                    )

                                    // Indicador de línea técnica (Solo si está seleccionado)
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
                            label = { /* El texto ya lo pusimos dentro de icon para control total */ }
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
                1 -> WikiScreen(viewmodel)
                2 -> SocialScreen()
                else -> HomeScreen()
            }
        }
    }
}


// Y necesitarías un ViewModelFactory
class ViewModelFactory(private val repository: OperatorRepositoryImpl) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OperatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OperatorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}