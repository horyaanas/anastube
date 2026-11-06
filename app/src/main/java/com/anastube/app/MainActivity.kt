package com.anastube.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anastube.app.data.DownloadState
import com.anastube.app.data.FormatOption
import com.anastube.app.data.MediaItem
import com.anastube.app.downloader.DownloadManager
import com.anastube.app.downloader.MediaExtractor
import com.anastube.app.ui.navigation.AnastubeBottomNavBar
import com.anastube.app.ui.navigation.Screen
import com.anastube.app.ui.screens.browser.BrowserScreen
import com.anastube.app.ui.screens.download.DownloadFormatSheet
import com.anastube.app.ui.screens.downloads.DownloadsScreen
import com.anastube.app.ui.screens.home.HomeScreen
import com.anastube.app.ui.screens.library.LibraryScreen
import com.anastube.app.ui.screens.player.PlayerScreen
import com.anastube.app.ui.screens.settings.SettingsScreen
import com.anastube.app.ui.theme.AnastubeTheme
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    private var sharedLinkFromIntent by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIncomingIntent(intent)

        setContent {
            AnastubeTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val context = LocalContext.current
                val downloads by DownloadManager.downloads.collectAsState()
                val activeCount = downloads.count { it.state != DownloadState.COMPLETED }

                // Global Download Sheet State
                var activeMediaForDownload by remember { mutableStateOf<MediaItem?>(null) }
                var availableFormats by remember { mutableStateOf<List<FormatOption>>(emptyList()) }

                // Check if opened via external "Share" link (SnapTube signature feature)
                LaunchedEffect(sharedLinkFromIntent) {
                    sharedLinkFromIntent?.let { url ->
                        val item = MediaItem(
                            id = "shared_${System.currentTimeMillis()}",
                            title = "Shared Media Link",
                            url = url,
                            thumbnailUrl = "https://images.unsplash.com/photo-1518791841217-8f162f1e1131?auto=format&fit=crop&w=400&q=80",
                            duration = "HD",
                            author = "Shared Source",
                            views = ""
                        )
                        availableFormats = MediaExtractor.resolveFormats(item)
                        activeMediaForDownload = item
                        sharedLinkFromIntent = null
                    }
                }

                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Downloads.route,
                    Screen.Library.route,
                    Screen.Settings.route
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            AnastubeBottomNavBar(
                                currentRoute = currentRoute,
                                activeDownloadCount = activeCount,
                                onNavigate = { screen ->
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route
                        ) {
                            composable(Screen.Home.route) {
                                HomeScreen(
                                    onNavigateToBrowser = { url ->
                                        val encoded = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                        navController.navigate("browser?url=$encoded")
                                    },
                                    onPlayMedia = { media ->
                                        val encodedUrl = URLEncoder.encode(media.url, StandardCharsets.UTF_8.toString())
                                        val encodedTitle = URLEncoder.encode(media.title, StandardCharsets.UTF_8.toString())
                                        navController.navigate(Screen.Player.createRoute(encodedUrl, encodedTitle))
                                    },
                                    onDownloadMedia = { media ->
                                        availableFormats = MediaExtractor.resolveFormats(media)
                                        activeMediaForDownload = media
                                    }
                                )
                            }

                            composable(
                                route = Screen.Browser.route,
                                arguments = listOf(navArgument("url") {
                                    type = NavType.StringType
                                    defaultValue = "https://m.youtube.com"
                                })
                            ) { backStack ->
                                val rawUrl = backStack.arguments?.getString("url") ?: "https://m.youtube.com"
                                val decodedUrl = try {
                                    URLDecoder.decode(rawUrl, StandardCharsets.UTF_8.toString())
                                } catch (e: Exception) {
                                    rawUrl
                                }

                                BrowserScreen(
                                    initialUrl = decodedUrl,
                                    onDownloadTriggered = { media ->
                                        availableFormats = MediaExtractor.resolveFormats(media)
                                        activeMediaForDownload = media
                                    }
                                )
                            }

                            composable(Screen.Downloads.route) {
                                DownloadsScreen(
                                    onPlayLocalFile = { path, title ->
                                        val encodedUrl = URLEncoder.encode(path, StandardCharsets.UTF_8.toString())
                                        val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
                                        navController.navigate(Screen.Player.createRoute(encodedUrl, encodedTitle))
                                    }
                                )
                            }

                            composable(Screen.Library.route) {
                                LibraryScreen(
                                    onPlayMedia = { path, name ->
                                        val encodedUrl = URLEncoder.encode(path, StandardCharsets.UTF_8.toString())
                                        val encodedTitle = URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
                                        navController.navigate(Screen.Player.createRoute(encodedUrl, encodedTitle))
                                    }
                                )
                            }

                            composable(Screen.Settings.route) {
                                SettingsScreen()
                            }

                            composable(
                                route = Screen.Player.route,
                                arguments = listOf(
                                    navArgument("url") { type = NavType.StringType },
                                    navArgument("title") { type = NavType.StringType }
                                )
                            ) { backStack ->
                                val rawUrl = backStack.arguments?.getString("url") ?: ""
                                val rawTitle = backStack.arguments?.getString("title") ?: "Video Player"
                                val decodedUrl = try {
                                    URLDecoder.decode(rawUrl, StandardCharsets.UTF_8.toString())
                                } catch (e: Exception) {
                                    rawUrl
                                }
                                val decodedTitle = try {
                                    URLDecoder.decode(rawTitle, StandardCharsets.UTF_8.toString())
                                } catch (e: Exception) {
                                    rawTitle
                                }

                                PlayerScreen(
                                    videoUrl = decodedUrl,
                                    title = decodedTitle,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }

                        // Download Bottom Sheet Overlay
                        activeMediaForDownload?.let { media ->
                            DownloadFormatSheet(
                                mediaItem = media,
                                formats = availableFormats,
                                onDismiss = { activeMediaForDownload = null },
                                onStartDownload = { format ->
                                    DownloadManager.enqueueDownload(context, media, format)
                                    activeMediaForDownload = null
                                    navController.navigate(Screen.Downloads.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrBlank()) {
                sharedLinkFromIntent = extractUrl(sharedText)
            }
        } else if (intent.action == Intent.ACTION_VIEW) {
            intent.dataString?.let {
                sharedLinkFromIntent = it
            }
        }
    }

    private fun extractUrl(text: String): String {
        val parts = text.split("\\s+".toRegex())
        return parts.firstOrNull { it.startsWith("http://") || it.startsWith("https://") } ?: text
    }
}
