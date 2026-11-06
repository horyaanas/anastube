package com.anastube.app.ui.screens.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.anastube.app.data.MediaItem
import com.anastube.app.downloader.MediaExtractor

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(
    initialUrl: String = "https://m.youtube.com",
    onDownloadTriggered: (MediaItem) -> Unit
) {
    var currentUrl by remember { mutableStateOf(initialUrl) }
    var inputUrl by remember { mutableStateOf(initialUrl) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var detectedMediaUrl by remember { mutableStateOf<String?>(null) }
    var pageTitle by remember { mutableStateOf("Browser") }

    val quickSites = remember {
        listOf(
            "YouTube" to "https://m.youtube.com",
            "Instagram" to "https://instagram.com",
            "TikTok" to "https://www.tiktok.com",
            "SoundCloud" to "https://soundcloud.com",
            "Facebook" to "https://m.facebook.com",
            "Twitter" to "https://x.com"
        )
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        OutlinedTextField(
                            value = inputUrl,
                            onValueChange = { inputUrl = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (webViewInstance?.canGoBack() == true) webViewInstance?.goBack()
                        }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (webViewInstance?.canGoForward() == true) webViewInstance?.goForward()
                        }) {
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Forward")
                        }
                        IconButton(onClick = {
                            webViewInstance?.reload()
                        }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Quick sites bar (SnapTube browser bookmarks row)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickSites) { (name, url) ->
                        FilterChip(
                            selected = currentUrl.contains(name.lowercase()),
                            onClick = {
                                currentUrl = url
                                inputUrl = url
                                webViewInstance?.loadUrl(url)
                            },
                            label = { Text(name, fontSize = 11.sp) },
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            // Floating Download Detector FAB (Signature SnapTube feature)
            AnimatedVisibility(
                visible = detectedMediaUrl != null || currentUrl.contains("watch") || currentUrl.contains("video"),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        val item = MediaItem(
                            id = "browser_${System.currentTimeMillis()}",
                            title = pageTitle.ifBlank { "Online Stream Video" },
                            url = detectedMediaUrl ?: currentUrl,
                            thumbnailUrl = "https://images.unsplash.com/photo-1518791841217-8f162f1e1131?auto=format&fit=crop&w=400&q=80",
                            duration = "HD",
                            author = "Web Stream",
                            views = ""
                        )
                        onDownloadTriggered(item)
                    },
                    containerColor = Color(0xFFFFBE00),
                    contentColor = Color.Black,
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Download Video", tint = Color.Black)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.mediaPlaybackRequiresUserGesture = false

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                url?.let {
                                    currentUrl = it
                                    inputUrl = it
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                pageTitle = view?.title ?: "Web Page"
                            }

                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                val reqUrl = request?.url?.toString() ?: ""
                                if (MediaExtractor.isMediaStream(reqUrl)) {
                                    detectedMediaUrl = reqUrl
                                }
                                return super.shouldInterceptRequest(view, request)
                            }
                        }

                        loadUrl(currentUrl)
                        webViewInstance = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
