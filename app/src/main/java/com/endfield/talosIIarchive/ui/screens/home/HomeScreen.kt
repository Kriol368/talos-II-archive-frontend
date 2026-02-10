package com.endfield.talosIIarchive.ui.screens.home

import android.R.attr.scaleX
import android.R.attr.scaleY
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldOrange
import com.endfield.talosIIarchive.ui.theme.EndfieldYellow
import com.endfield.talosIIarchive.ui.theme.TechBackground
import com.endfield.talosIIarchive.ui.theme.TechBorder
import com.endfield.talosIIarchive.ui.theme.TechSurface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class HomeViewModel : ViewModel() {
    private val client = OkHttpClient()

    suspend fun fetchEndfieldImages(): List<String> = withContext(Dispatchers.IO) {
        val imageUrls = mutableListOf<String>()

        try {
            val url = "https://www.reddit.com/r/Endfield/hot.json?limit=100"
            val request =
                Request.Builder().url(url).addHeader("User-Agent", "TalosArchive/1.0").build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val json = JSONObject(responseBody)
                val data = json.getJSONObject("data")
                val children = data.getJSONArray("children")

                for (i in 0 until children.length()) {
                    val child = children.getJSONObject(i)
                    val post = child.getJSONObject("data")

                    val imageUrl = when {
                        post.optString("post_hint", "") == "image" -> {
                            post.optString("url", "")
                        }

                        post.has("preview") -> {
                            val preview = post.optJSONObject("preview")
                            val images = preview?.optJSONArray("images")
                            images?.getJSONObject(0)?.optJSONObject("source")?.optString("url", "")
                                ?.replace("&amp;", "&")
                        }

                        else -> null
                    }

                    if (imageUrl != null && imageUrl.startsWith("http")) {
                        imageUrls.add(imageUrl)
                    }

                    if (imageUrls.size >= 20) break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext imageUrls
    }
}

@Composable
fun HomeScreen(
    onWikiClick: () -> Unit = {},
    onSocialClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {

    var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(pageCount = { imageUrls.size })


    // Estados para el drag del footer
    var footerOffsetX by remember { mutableFloatStateOf(0f) }
    var footerOffsetY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        isLoading = true
        imageUrls = viewModel.fetchEndfieldImages()
        isLoading = false
    }

    LaunchedEffect(imageUrls) {
        if (imageUrls.isNotEmpty()) {
            while (true) {
                yield() // Cede el paso para evitar bloqueos de UI
                delay(2500) // Tiempo de espera entre transiciones

                // Solo animamos si el usuario NO está tocando el pager actualmente
                if (!pagerState.isScrollInProgress) {
                    val nextPage = (pagerState.currentPage + 1) % imageUrls.size
                    try {
                        pagerState.animateScrollToPage(
                            page = nextPage,
                            animationSpec = tween(
                                durationMillis = 1000,
                                easing = FastOutSlowInEasing
                            )
                        )
                    } catch (e: Exception) {
                        // Si algo cancela la animación, el bucle simplemente sigue al siguiente ciclo
                        println("Auto-scroll interrumpido: ${e.message}")
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TechBackground)
            .padding(16.dp)
    ) {
        // Header con estilo de tu tema
        Column(
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Text(
                text = "// SYSTEM_DASHBOARD",
                color = EndfieldYellow,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "TALOS-II ARCHIVE",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(EndfieldOrange)
                    .padding(top = 4.dp)
            )
        }

        // Sección trending (SIN CAMBIOS - mantiene las imágenes normales)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = TechSurface
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NETWORK // REDDIT_FEED",
                            color = EndfieldCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "TRENDING ON r/ENDFIELD",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "REAL-TIME",
                            color = EndfieldYellow,
                            fontSize = 6.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = EndfieldOrange)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "ACCESSING_NETWORK...",
                                color = EndfieldOrange,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }else if (imageUrls.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxSize()) { // Este Box agrupa todo
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            // Calculamos la distancia de la página actual al centro
                            val pageOffset = (pagerState.currentPage - page + pagerState.currentPageOffsetFraction).absoluteValue

                            Box(modifier = Modifier.graphicsLayer {
                                // Animamos la transparencia según la posición
                                alpha = lerp(
                                    start = 0.1f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                            }) {
                                FeaturedImageCard(imageUrl = imageUrls[page])
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        // Botón WIKI con estilo consistente
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .border(1.dp, TechBorder)
                .clickable { onWikiClick() },
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = TechSurface
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Barra lateral naranja
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(6.dp)
                        .background(EndfieldOrange)
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Número de módulo
                    Column(
                        modifier = Modifier
                            .width(60.dp)
                            .padding(end = 16.dp)
                    ) {
                        Text(
                            text = "ID.01",
                            color = EndfieldOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(EndfieldOrange)
                                .padding(top = 2.dp)
                        )
                    }

                    // Contenido del botón
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "WIKI",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "ACCESS_FILES",
                            color = EndfieldOrange,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    // Indicador de acceso
                    Box(
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ACCESS >",
                            color = EndfieldYellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón SOCIAL con estilo consistente
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .border(1.dp, TechBorder)
                .clickable { onSocialClick() },
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = TechSurface
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Barra lateral cian
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(6.dp)
                        .background(EndfieldCyan)
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Número de módulo
                    Column(
                        modifier = Modifier
                            .width(60.dp)
                            .padding(end = 16.dp)
                    ) {
                        Text(
                            text = "ID.02",
                            color = EndfieldCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(EndfieldCyan)
                                .padding(top = 2.dp)
                        )
                    }

                    // Contenido del botón
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "SOCIAL",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "COMUNITY_UPLOADS",
                            color = EndfieldCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    // Indicador de acceso
                    Box(
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ACCESS >",
                            color = EndfieldYellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Footer CON DRAG GESTURE
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .border(1.dp, TechBorder)
                .offset { IntOffset(footerOffsetX.roundToInt(), footerOffsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            footerOffsetX += dragAmount.x
                            footerOffsetY += dragAmount.y
                        },
                        onDragEnd = {
                            // Vuelve a la posición original al soltar
                            footerOffsetX = 0f
                            footerOffsetY = 0f
                        }
                    )
                }
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "// SYSTEM_STATUS: ONLINE",
                    color = EndfieldYellow,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "TALOS_OS v2.0.1",
                    color = Color.Gray,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FeaturedImageCard(imageUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .border(1.dp, TechBorder)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay de "Interferencias" (Estética Endfield)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                    )
                )
        )


    }
}

@Composable
fun EndfieldImageCard(imageUrl: String) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(200.dp)
            .border(0.5.dp, TechBorder),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                strokeWidth = 2.dp,
                                color = EndfieldOrange
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "LOADING...",
                                color = EndfieldOrange,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                contentDescription = "Arknights Endfield image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Overlay degradado en la parte inferior
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 100f,
                            endY = 400f
                        )
                    )
            )

            // Badge de fuente
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(Color.Black)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "r/ENDFIELD",
                    color = EndfieldCyan,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}