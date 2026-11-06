package com.anastube.app.ui.screens.player

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.anastube.app.data.MediaItem
import com.anastube.app.data.YouTubeRepository

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouTubePlayerScreen(
    mediaItem: MediaItem,
    onMinimize: () -> Unit,
    onDownloadCurrent: () -> Unit,
    onPlayRelated: (MediaItem) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var relatedVideos by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var isFullscreen by remember { mutableStateOf(false) }

    LaunchedEffect(mediaItem.id) {
        val query = mediaItem.author.ifBlank { mediaItem.title.take(15) }
        relatedVideos = YouTubeRepository.searchVideos(query)
    }

    val videoId = mediaItem.id.ifBlank {
        val pattern = "(?:v=|youtu\\.be/)([a-zA-Z0-9_-]{11})".toRegex()
        pattern.find(mediaItem.url)?.groupValues?.get(1) ?: "dQw4w9WgXcQ"
    }

    val playerHtml = remember(videoId) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; background: #000; }
                html, body { width: 100%; height: 100%; overflow: hidden; background: #000; }
                iframe { width: 100%; height: 100%; border: none; }
            </style>
        </head>
        <body>
            <iframe 
                src="https://www.youtube.com/embed/$videoId?autoplay=1&playsinline=1&controls=1&fs=1&rel=0&modestbranding=1" 
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                allowfullscreen>
            </iframe>
        </body>
        </html>
        """.trimIndent()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Player Surface Container (No Browser URL Bar, Pure Native Player Appearance)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isFullscreen) 340.dp else 235.dp)
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.cacheMode = WebSettings.LOAD_DEFAULT

                        webChromeClient = WebChromeClient()
                        webViewClient = object : WebViewClient() {}

                        loadDataWithBaseURL("https://www.youtube.com", playerHtml, "text/html", "UTF-8", null)
                    }
                },
                update = { webView ->
                    webView.loadDataWithBaseURL("https://www.youtube.com", playerHtml, "text/html", "UTF-8", null)
                },
                modifier = Modifier.fillMaxSize()
            )

            // Minimize Arrow (top-left) to drop into MiniPlayerBar
            IconButton(
                onClick = onMinimize,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Minimize",
                    tint = Color.White
                )
            }

            // Fullscreen Toggle (bottom-right)
            IconButton(
                onClick = {
                    isFullscreen = !isFullscreen
                    if (isFullscreen) {
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    } else {
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                    contentDescription = "Fullscreen",
                    tint = Color.White
                )
            }
        }

        // Details & Recommendations Section (YouTube native style)
        if (!isFullscreen) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Video Title
                item {
                    Text(
                        text = mediaItem.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${mediaItem.views} • ${mediaItem.duration}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Channel Info Card
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = mediaItem.author.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mediaItem.author,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "YouTube Official Channel",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // SnapTube Primary Action Bar: Big Yellow/Blue Download Button
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onDownloadCurrent,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download (MP3 / Video)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        FilledTonalIconButton(
                            onClick = { /* Share */ },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }

                item {
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Up Next & Related Videos",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Related Videos List
                items(relatedVideos.filter { it.id != mediaItem.id }) { related ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onPlayRelated(related) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 120.dp, height = 75.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = related.thumbnailUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                color = Color.Black.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = related.duration,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = related.title,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${related.author} • ${related.views}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}
