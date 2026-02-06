package com.endfield.talosIIarchive.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

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

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoading = true
            imageUrls = viewModel.fetchEndfieldImages()
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E17))
            .padding(16.dp)
    ) {
        Text(
            text = "TALOS-II ARCHIVE",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CC9F0),
            modifier = Modifier.padding(bottom = 8.dp)
        )


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A2E)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(
                    text = "Now trending on r/Endfield",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4CC9F0))
                    }
                } else if (imageUrls.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(imageUrls) { imageUrl ->
                            EndfieldImageCard(imageUrl = imageUrl)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No images found", color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onWikiClick()},
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E293B)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF4CC9F0)), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "W",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "WIKI",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {onSocialClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A1A3A)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF9D4EDD)), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "C",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "SOCIAL",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun EndfieldImageCard(imageUrl: String) {
    val targetHeight = 200.dp

    Card(
        modifier = Modifier
            .wrapContentWidth()
            .height(targetHeight),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF252525)
        )
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true)
                .build(),
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF4CC9F0)
                    )
                }
            },
            contentDescription = "Arknights Endfield image",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxHeight()
        )
    }
}