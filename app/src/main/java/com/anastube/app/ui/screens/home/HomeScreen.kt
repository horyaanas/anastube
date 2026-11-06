package com.anastube.app.ui.screens.home

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anastube.app.data.BookmarkItem
import com.anastube.app.data.MediaItem
import com.anastube.app.data.YouTubeRepository
import com.anastube.app.downloader.MediaExtractor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToBrowser: (String) -> Unit,
    onPlayMedia: (MediaItem) -> Unit,
    onDownloadMedia: (MediaItem) -> Unit,
    onOpenVault: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    // Top Tabs: [بحث, YouTube, الموسيقى, المزيد]
    val topTabs = listOf("بحث", "YouTube", "الموسيقى", "المزيد")
    var selectedTopTab by remember { mutableStateOf(0) }

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<MediaItem>>(emptyList()) }

    var trendingVideos by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var musicSection1 by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var musicSection2 by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var musicSection3 by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var isLoadingFeed by remember { mutableStateOf(false) }

    // Clipboard Sniffer
    var copiedLinkDetected by remember { mutableStateOf<String?>(null) }

    // Fetch initial content
    LaunchedEffect(Unit) {
        isLoadingFeed = true
        trendingVideos = YouTubeRepository.getTrendingVideos()
        isLoadingFeed = false

        // Load music feeds in background
        scope.launch {
            musicSection1 = YouTubeRepository.searchVideos("أجمل الأغاني اليوم")
            musicSection2 = YouTubeRepository.searchVideos("عربي جديد 2026")
            musicSection3 = YouTubeRepository.searchVideos("أفروبيتس وموسيقى هادئة")
        }

        // Check clipboard
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
        if (!clip.isNullOrBlank() && MediaExtractor.isMediaShareUrl(clip)) {
            copiedLinkDetected = clip
        }
    }

    val quickSites = listOf(
        BookmarkItem("yt", "YouTube", "https://m.youtube.com", 0xFFFF0000),
        BookmarkItem("fb", "Facebook", "https://m.facebook.com", 0xFF1877F2),
        BookmarkItem("ig", "Instagram", "https://instagram.com", 0xFFE1306C),
        BookmarkItem("tk", "TikTok", "https://www.tiktok.com", 0xFF000000),
        BookmarkItem("wa", "حالات WhatsApp", "whatsapp://status", 0xFF25D366),
        BookmarkItem("sc", "SoundCloud", "https://soundcloud.com", 0xFFFF5500),
        BookmarkItem("tw", "Twitter / X", "https://x.com", 0xFF1DA1F2),
        BookmarkItem("vt", "الخزنة", "vault://local", 0xFF1976D2)
    )

    val quickSuggestions = listOf("أغاني تريند", "قرآن كريم", "شيلات جديدة", "أناشيد حماسية", "أفلام 2026", "بودكاست")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top SnapTube Tab Bar: [بحث, YouTube, الموسيقى, المزيد]
        TabRow(
            selectedTabIndex = selectedTopTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            topTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTopTab == index,
                    onClick = { selectedTopTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTopTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp,
                            color = if (selectedTopTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                )
            }
        }

        // Tab Content
        when (selectedTopTab) {
            0 -> SearchHomeTabContent(
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { query ->
                    focusManager.clearFocus()
                    if (query.isNotBlank()) {
                        if (query.startsWith("http://") || query.startsWith("https://")) {
                            onNavigateToBrowser(query)
                        } else {
                            scope.launch {
                                isSearching = true
                                searchResults = YouTubeRepository.searchVideos(query)
                                isSearching = false
                            }
                        }
                    }
                },
                isSearching = isSearching,
                searchResults = searchResults,
                copiedLink = copiedLinkDetected,
                onDismissCopiedLink = { copiedLinkDetected = null },
                onDownloadCopiedLink = { link ->
                    val dummy = MediaItem(
                        id = "copied_${System.currentTimeMillis()}",
                        title = "Copied Media Link",
                        url = link,
                        thumbnailUrl = "https://images.unsplash.com/photo-1518791841217-8f162f1e1131?auto=format&fit=crop&w=400&q=80",
                        duration = "HD",
                        author = "Shared Link",
                        views = ""
                    )
                    onDownloadMedia(dummy)
                    copiedLinkDetected = null
                },
                quickSites = quickSites,
                quickSuggestions = quickSuggestions,
                trendingVideos = trendingVideos,
                onNavigateToBrowser = onNavigateToBrowser,
                onPlayMedia = onPlayMedia,
                onDownloadMedia = onDownloadMedia,
                onOpenVault = onOpenVault
            )

            1 -> YouTubeTabContent(
                videos = trendingVideos,
                isLoading = isLoadingFeed,
                onPlayMedia = onPlayMedia,
                onDownloadMedia = onDownloadMedia
            )

            2 -> MusicTabContent(
                section1 = musicSection1,
                section2 = musicSection2,
                section3 = musicSection3,
                onPlayMedia = onPlayMedia,
                onDownloadMedia = onDownloadMedia
            )

            3 -> MoreTabContent(
                onOpenVault = onOpenVault,
                onNavigateToBrowser = onNavigateToBrowser
            )
        }
    }
}

// -------------------------------------------------------------
// TAB 0: SnapTube Search / Home Landing (Image 1)
// -------------------------------------------------------------
@Composable
private fun SearchHomeTabContent(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    isSearching: Boolean,
    searchResults: List<MediaItem>,
    copiedLink: String?,
    onDismissCopiedLink: () -> Unit,
    onDownloadCopiedLink: (String) -> Unit,
    quickSites: List<BookmarkItem>,
    quickSuggestions: List<String>,
    trendingVideos: List<MediaItem>,
    onNavigateToBrowser: (String) -> Unit,
    onPlayMedia: (MediaItem) -> Unit,
    onDownloadMedia: (MediaItem) -> Unit,
    onOpenVault: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        // Centered App Brand Logo (Like SnapTube's centered brand in Screenshot 1)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "anastube",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "مشغل وداونلودر بدون إعلانات",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Pill-Shaped Search Bar (Exact match to Screenshot 1 "ابحث للتنزيل")
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        TextField(
                            value = searchQuery,
                            onValueChange = onQueryChange,
                            placeholder = {
                                Text(
                                    text = "ابحث للتنزيل أو أدخل الرابط...",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { onSearch(searchQuery) }),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onSearch(searchQuery) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = "Download",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Clipboard Sniffer Banner (SnapTube Smart Downloader)
        if (copiedLink != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "رابط تم اكتشافه في الحافظة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = copiedLink.take(35) + "...",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Button(
                            onClick = { onDownloadCopiedLink(copiedLink) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("تنزيل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = onDismissCopiedLink) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Quick Sites Grid (SnapTube Shortcuts)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp, start = 20.dp, end = 20.dp)
            ) {
                Text(
                    text = "المواقع الشهيرة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                // 4x2 Grid of Quick Sites
                val row1 = quickSites.take(4)
                val row2 = quickSites.drop(4).take(4)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row1.forEach { site ->
                        QuickSiteItem(
                            item = site,
                            onClick = {
                                if (site.id == "vt") onOpenVault()
                                else onNavigateToBrowser(site.url)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row2.forEach { site ->
                        QuickSiteItem(
                            item = site,
                            onClick = {
                                if (site.id == "vt") onOpenVault()
                                else onNavigateToBrowser(site.url)
                            }
                        )
                    }
                }
            }
        }

        // Quick Suggestions Horizontal Chips
        item {
            Spacer(modifier = Modifier.height(20.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickSuggestions) { tag ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.clickable {
                            onQueryChange(tag)
                            onSearch(tag)
                        }
                    ) {
                        Text(
                            text = tag,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Search Results or Recommended Videos
        if (isSearching) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        } else if (searchResults.isNotEmpty()) {
            item {
                Text(
                    text = "نتائج البحث (${searchResults.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 10.dp)
                )
            }
            items(searchResults) { video ->
                VideoCardItem(
                    video = video,
                    onPlay = { onPlayMedia(video) },
                    onDownload = { onDownloadMedia(video) }
                )
            }
        } else {
            item {
                Text(
                    text = "فيديوهات مقترحة للتنزيل",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 10.dp)
                )
            }
            items(trendingVideos) { video ->
                VideoCardItem(
                    video = video,
                    onPlay = { onPlayMedia(video) },
                    onDownload = { onDownloadMedia(video) }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 1: YouTube Native Tab (Image 2)
// -------------------------------------------------------------
@Composable
private fun YouTubeTabContent(
    videos: List<MediaItem>,
    isLoading: Boolean,
    onPlayMedia: (MediaItem) -> Unit,
    onDownloadMedia: (MediaItem) -> Unit
) {
    if (isLoading && videos.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
        ) {
            items(videos) { video ->
                VideoCardItem(
                    video = video,
                    onPlay = { onPlayMedia(video) },
                    onDownload = { onDownloadMedia(video) }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: Music Tab (Image 3)
// -------------------------------------------------------------
@Composable
private fun MusicTabContent(
    section1: List<MediaItem>,
    section2: List<MediaItem>,
    section3: List<MediaItem>,
    onPlayMedia: (MediaItem) -> Unit,
    onDownloadMedia: (MediaItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        // Carousel 1: أجمل الأغاني اليوم
        item {
            MusicSectionCarousel(
                title = "أجمل الأغاني اليوم",
                items = section1,
                onPlay = onPlayMedia,
                onDownload = onDownloadMedia
            )
        }

        // Carousel 2: عربي جديد
        item {
            Spacer(modifier = Modifier.height(20.dp))
            MusicSectionCarousel(
                title = "عربي جديد 2026",
                items = section2,
                onPlay = onPlayMedia,
                onDownload = onDownloadMedia
            )
        }

        // Carousel 3: أفروبيتس وموسيقى هادئة
        item {
            Spacer(modifier = Modifier.height(20.dp))
            MusicSectionCarousel(
                title = "أفروبيتس وموسيقى هادئة",
                items = section3,
                onPlay = onPlayMedia,
                onDownload = onDownloadMedia
            )
        }
    }
}

@Composable
private fun MusicSectionCarousel(
    title: String,
    items: List<MediaItem>,
    onPlay: (MediaItem) -> Unit,
    onDownload: (MediaItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "عرض الكل",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(items) { item ->
                    MusicCardItem(
                        item = item,
                        onPlay = { onPlay(item) },
                        onDownload = { onDownload(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MusicCardItem(
    item: MediaItem,
    onPlay: () -> Unit,
    onDownload: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(135.dp)
            .clickable { onPlay() }
    ) {
        Box(
            modifier = Modifier
                .size(135.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            AsyncImage(
                model = item.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Play overlay on image
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Download arrow icon at bottom right of thumbnail
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .size(28.dp)
                    .clickable { onDownload() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Download",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = item.author,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// -------------------------------------------------------------
// TAB 3: More Tab (Tools & Supported Sites)
// -------------------------------------------------------------
@Composable
private fun MoreTabContent(
    onOpenVault: () -> Unit,
    onNavigateToBrowser: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "أدوات وميزات Anastube",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            ToolCardItem(
                icon = Icons.Default.Lock,
                title = "الخزنة المشفرة",
                subtitle = "احمِ فيديوهاتك وملفاتك الحساسة برقم سري مشفر",
                color = MaterialTheme.colorScheme.primary,
                onClick = onOpenVault
            )
        }

        item {
            ToolCardItem(
                icon = Icons.Default.ChatBubble,
                title = "منزل حالات WhatsApp",
                subtitle = "حفظ حالات الفيديو والصور فوراً إلى هاتفك",
                color = Color(0xFF25D366),
                onClick = { /* Status Saver */ }
            )
        }

        item {
            ToolCardItem(
                icon = Icons.Default.MusicNote,
                title = "محول MP3 فائق السرعة",
                subtitle = "استخراج الصوت بجودة تصل إلى 320kbps بدون فقدان",
                color = Color(0xFFFF5500),
                onClick = { /* Audio extractor */ }
            )
        }

        item {
            ToolCardItem(
                icon = Icons.Default.Public,
                title = "متصفح مانع الإعلانات",
                subtitle = "تصفح أي موقع وتنزيل الفيديوهات بضغطة زر واحدة",
                color = Color(0xFF1976D2),
                onClick = { onNavigateToBrowser("https://m.youtube.com") }
            )
        }
    }
}

@Composable
private fun ToolCardItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null, tint = Color.Gray)
        }
    }
}

// -------------------------------------------------------------
// Video Card Component (Exact Match to Screenshot 2)
// -------------------------------------------------------------
@Composable
private fun VideoCardItem(
    video: MediaItem,
    onPlay: () -> Unit,
    onDownload: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlay() }
            .padding(bottom = 18.dp)
    ) {
        // Thumbnail with 16:9 ratio and duration badge bottom-left
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(205.dp)
        ) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Duration Pill (bottom-left / bottom-right)
            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = video.duration,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Details Row below thumbnail (Channel Avatar, Title, Channel Name, Download Icon)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Channel Avatar
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = video.author.take(1).ifBlank { "A" },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Title & Channel / Views
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${video.author} • ${video.views}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // SnapTube Circular Download Button (Screenshot 2: down arrow inside circle)
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onDownload() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Download",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Quick Site Circle / Squircle
// -------------------------------------------------------------
@Composable
private fun QuickSiteItem(
    item: BookmarkItem,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable { onClick() }
    ) {
        Surface(
            shape = CircleShape,
            color = Color(item.colorInt),
            modifier = Modifier.size(50.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                val icon = when (item.id) {
                    "yt" -> Icons.Default.PlayArrow
                    "fb" -> Icons.Default.ThumbUp
                    "ig" -> Icons.Default.CameraAlt
                    "tk" -> Icons.Default.MusicVideo
                    "wa" -> Icons.Default.ChatBubble
                    "sc" -> Icons.Default.Headphones
                    "tw" -> Icons.Default.Tag
                    "vt" -> Icons.Default.Lock
                    else -> Icons.Default.Public
                }
                Icon(imageVector = icon, contentDescription = item.title, tint = Color.White, modifier = Modifier.size(26.dp))
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
