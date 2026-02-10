package com.endfield.talosIIarchive.ui.screens.home

import android.content.res.Configuration
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.endfield.talosIIarchive.ui.theme.EndfieldCyan
import com.endfield.talosIIarchive.ui.theme.EndfieldLightGrey
import com.endfield.talosIIarchive.ui.theme.EndfieldOrange
import com.endfield.talosIIarchive.ui.theme.TechBackground
import com.endfield.talosIIarchive.ui.theme.TechBorder
import com.endfield.talosIIarchive.ui.theme.TechSurface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
                        post.optString("post_hint", "") == "image" -> post.optString("url", "")
                        post.has("preview") -> {
                            val preview = post.optJSONObject("preview")
                            val images = preview?.optJSONArray("images")
                            images?.getJSONObject(0)?.optJSONObject("source")?.optString("url", "")
                                ?.replace("&amp;", "&")
                        }

                        else -> null
                    }
                    if (imageUrl != null && imageUrl.startsWith("http")) imageUrls.add(imageUrl)
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
    val pagerState = rememberPagerState(pageCount = { imageUrls.size })
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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
                yield()
                delay(2500)
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
                    } catch (_: Exception) {
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
        HeaderSection(isLandscape)

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1.2f)) {
                    RedditFeedSection(isLoading, imageUrls, pagerState)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MenuButton(
                        id = "ID.S1",
                        title = "WIKI",
                        subtitle = "ACCESS_FILES",
                        accentColor = EndfieldOrange,
                        onClick = onWikiClick
                    )
                    MenuButton(
                        id = "ID.S2",
                        title = "SOCIAL",
                        subtitle = "COMMUNITY_UPLOADS",
                        accentColor = EndfieldCyan,
                        onClick = onSocialClick
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FooterStatus(
                        offsetX = footerOffsetX,
                        offsetY = footerOffsetY,
                        onDrag = { dx, dy -> footerOffsetX += dx; footerOffsetY += dy },
                        onDragEnd = { footerOffsetX = 0f; footerOffsetY = 0f }
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier.height(320.dp)) {
                    RedditFeedSection(isLoading, imageUrls, pagerState)
                }

                Spacer(modifier = Modifier.height(16.dp))

                MenuButton(
                    id = "ID.S1",
                    title = "WIKI",
                    subtitle = "ACCESS_FILES",
                    accentColor = EndfieldOrange,
                    onClick = onWikiClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                MenuButton(
                    id = "ID.S2",
                    title = "SOCIAL",
                    subtitle = "COMMUNITY_UPLOADS",
                    accentColor = EndfieldCyan,
                    onClick = onSocialClick
                )

                Spacer(modifier = Modifier.height(32.dp))

                FooterStatus(
                    offsetX = footerOffsetX,
                    offsetY = footerOffsetY,
                    onDrag = { dx, dy -> footerOffsetX += dx; footerOffsetY += dy },
                    onDragEnd = { footerOffsetX = 0f; footerOffsetY = 0f }
                )
            }
        }
    }
}

@Composable
fun HeaderSection(isLandscape: Boolean) {
    Column {
        Text(
            text = "// SYSTEM_DASHBOARD",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = "TALOS-II ARCHIVE",
            fontSize = if (isLandscape) 24.sp else 32.sp,
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
}

@Composable
fun RedditFeedSection(
    isLoading: Boolean,
    imageUrls: List<String>,
    pagerState: androidx.compose.foundation.pager.PagerState
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = TechSurface),
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
                        "TRENDING ON r/ENDFIELD",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = EndfieldOrange)
                }
            } else if (imageUrls.isNotEmpty()) {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    val pageOffset =
                        (pagerState.currentPage - page + pagerState.currentPageOffsetFraction).absoluteValue
                    Box(modifier = Modifier.graphicsLayer {
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

@Composable
fun MenuButton(
    id: String,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .border(1.dp, TechBorder)
            .clickable { onClick() },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = TechSurface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(accentColor)
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    Modifier
                        .width(60.dp)
                        .padding(end = 16.dp)
                ) {
                    Text(id, color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(accentColor)
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        subtitle,
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    Modifier
                        .background(Color.Black)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "ACCESS >",
                        color = EndfieldLightGrey,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FooterStatus(
    offsetX: Float,
    offsetY: Float,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TechBorder)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    },
                    onDragEnd = { onDragEnd() }
                )
            }
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "// SYSTEM_STATUS: ONLINE",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "TALOS_OS v1.0.0",
                color = Color.Gray,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f)
                        )
                    )
                )
        )
    }
}