package com.endfield.talosIIarchive.ui.screens.home

import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.webkit.WebViewAssetLoader
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.endfield.talosIIarchive.R
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
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
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

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0E17))) {

        // --- CAPA 1 (FONDO): TU INTERFAZ ---
        // Ahora la interfaz va primero para estar debajo
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                modifier = Modifier.fillMaxWidth().height(250.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E))
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    Text("Now trending on r/Endfield", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (isLoading) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = Color(0xFF4CC9F0)) }
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(imageUrls) { url -> EndfieldImageCard(url) }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            MenuButton("WIKI", "W", Color(0xFF4CC9F0))
            Spacer(modifier = Modifier.height(16.dp))
            MenuButton("SOCIAL", "C", Color(0xFF9D4EDD))
        }

        // --- CAPA 2 (TOP): EL MODELO 3D ---
        // Al estar al final, se dibuja ENCIMA de la interfaz
        ModelViewer3D(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun ModelViewer3D(modifier: Modifier) {
    val context = LocalContext.current
    val assetLoader = remember {
        WebViewAssetLoader.Builder()
            .setDomain("appassets.android.com")
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
            .build()
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                settings.javaScriptEnabled = true

                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(
                        view: WebView,
                        request: WebResourceRequest
                    ): WebResourceResponse? {
                        return assetLoader.shouldInterceptRequest(request.url)
                    }
                }

                val html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <script type="module" src="https://ajax.googleapis.com/ajax/libs/model-viewer/3.3.0/model-viewer.min.js"></script>
                        <style>
                            body { margin: 0; padding: 0; background: transparent; overflow: hidden; }
                            model-viewer { 
                                width: 200vw; 
                                height: 200vh; 
                                background: transparent;
                                --poster-color: transparent;
                                pointer-events: none; 
                            }
                        </style>
                    </head>
                    <body>
                        <model-viewer 
                            src="https://appassets.android.com/assets/talos_animado.glb" 
                            autoplay 
                            animation-name="*"
                            /* ESTO MEJORA EL COLOR: */
    environment-image="neutral" 
    exposure="1.2" 
    shadow-intensity="1.5"
    shadow-softness="1"
    /* --------------------- */
                            shadow-intensity="1"
                            /* camera-orbit: giro, inclinación y distancia (3m es más cerca que 4m) */
                            camera-orbit="0deg 90deg 2m" 
                            /* camera-target: X Y Z. El valor central (0.8m) sube el modelo al centro visual */
                            camera-target="250m 0m 0m" 
                            interaction-prompt="none">
                        </model-viewer>
                    </body>
                    </html>
                """.trimIndent()

                loadDataWithBaseURL("https://appassets.android.com/", html, "text/html", "UTF-8", null)
            }
        }
    )
}

@Composable
fun MenuButton(text: String, icon: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.8f))
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(color), Alignment.Center) {
                Text(icon, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
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