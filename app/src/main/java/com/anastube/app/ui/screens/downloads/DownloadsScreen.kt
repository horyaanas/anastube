package com.anastube.app.ui.screens.downloads

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anastube.app.data.DownloadItem
import com.anastube.app.data.DownloadState
import com.anastube.app.downloader.DownloadManager
import com.anastube.app.ui.theme.SnapTubeYellow
import com.anastube.app.ui.theme.SnapTubeYellowDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    onPlayLocalFile: (String, String) -> Unit
) {
    val context = LocalContext.current
    val downloads by DownloadManager.downloads.collectAsState()

    // 0: Downloading (قيد التنزيل), 1: Downloaded (المكتملة)
    var selectedTopTab by remember { mutableStateOf(0) }
    var selectedFilter by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val activeDownloads = downloads.filter { it.state != DownloadState.COMPLETED }
    val completedDownloads = downloads.filter { it.state == DownloadState.COMPLETED }

    val filteredCompleted = completedDownloads.filter { item ->
        val matchesCategory = when (selectedFilter) {
            "فيديو" -> !item.isAudio
            "موسيقى" -> item.isAudio
            else -> true
        }
        val matchesQuery = if (searchQuery.isBlank()) true else item.title.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        if (isSearchActive) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("بحث في الملفات...") },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ملفاتي (My Files)",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(
                                imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                // Sub Tab Row: [قيد التنزيل (N), المكتملة (M)]
                TabRow(
                    selectedTabIndex = selectedTopTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = SnapTubeYellow,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTopTab]),
                            color = SnapTubeYellow,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTopTab == 0,
                        onClick = { selectedTopTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "قيد التنزيل",
                                    fontWeight = if (selectedTopTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                if (activeDownloads.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = SnapTubeYellow,
                                        modifier = Modifier.padding(2.dp)
                                    ) {
                                        Text(
                                            text = "${activeDownloads.size}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    )
                    Tab(
                        selected = selectedTopTab == 1,
                        onClick = { selectedTopTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "المكتملة",
                                    fontWeight = if (selectedTopTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                if (completedDownloads.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.padding(2.dp)
                                    ) {
                                        Text(
                                            text = "${completedDownloads.size}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (selectedTopTab == 0) {
                // Active Downloads Tab
                if (activeDownloads.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                modifier = Modifier.size(64.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد مهام قيد التنزيل حالياً",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "تصفح يوتيوب والمواقع لتنزيل الفيديوهات والصوتيات",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Quick Action: Pause All / Resume All
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "المهام الجارية (${activeDownloads.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Row {
                                    OutlinedButton(
                                        onClick = {
                                            activeDownloads.forEach { DownloadManager.resumeDownload(it.id) }
                                            Toast.makeText(context, "تم استئناف الكل", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                    ) {
                                        Text("استئناف الكل", fontSize = 11.sp, color = SnapTubeYellowDark)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    OutlinedButton(
                                        onClick = {
                                            activeDownloads.forEach { DownloadManager.pauseDownload(it.id) }
                                            Toast.makeText(context, "تم إيقاف الكل مؤقتاً", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                    ) {
                                        Text("إيقاف مؤقت", fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        items(activeDownloads, key = { it.id }) { item ->
                            ActiveDownloadCard(
                                item = item,
                                onPause = { DownloadManager.pauseDownload(item.id) },
                                onResume = { DownloadManager.resumeDownload(item.id) },
                                onCancel = { DownloadManager.deleteDownload(item.id) }
                            )
                        }
                    }
                }
            } else {
                // Completed Downloads Tab
                Column(modifier = Modifier.fillMaxSize()) {
                    // Category Filters Row: [الكل, فيديو, موسيقى]
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(listOf("الكل", "فيديو", "موسيقى")) { filter ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = { Text(filter, fontSize = 12.sp) },
                                shape = RoundedCornerShape(16.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SnapTubeYellow,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    if (filteredCompleted.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    modifier = Modifier.size(64.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FolderOpen,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "لا توجد ملفات منزلة في هذا القسم",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredCompleted, key = { it.id }) { item ->
                                CompletedDownloadCard(
                                    item = item,
                                    onPlay = { onPlayLocalFile(item.destinationPath, item.title) },
                                    onDelete = { DownloadManager.deleteDownload(item.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveDownloadCard(
    item: DownloadItem,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = item.thumbnailUrl.ifBlank { "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=200&q=80" },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 68.dp, height = 48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SnapTubeYellow.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${item.format} • ${item.quality}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SnapTubeYellowDark,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.speed,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Row {
                    if (item.state == DownloadState.PAUSED) {
                        IconButton(onClick = onResume) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Resume", tint = SnapTubeYellowDark)
                        }
                    } else {
                        IconButton(onClick = onPause) {
                            Icon(imageVector = Icons.Default.Pause, contentDescription = "Pause")
                        }
                    }
                    IconButton(onClick = onCancel) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SnapTube Yellow Progress Bar
            LinearProgressIndicator(
                progress = { item.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = SnapTubeYellow,
                trackColor = MaterialTheme.colorScheme.surface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${item.progress}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SnapTubeYellowDark
                )
                Text(
                    text = item.totalSize,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun CompletedDownloadCard(
    item: DownloadItem,
    onPlay: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlay() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 76.dp, height = 52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = item.thumbnailUrl.ifBlank { "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=200&q=80" },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(26.dp),
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = SnapTubeYellow,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = if (item.isAudio) Color(0xFFFF9100).copy(alpha = 0.2f) else SnapTubeYellow.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = item.format,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.isAudio) Color(0xFFFF9100) else SnapTubeYellowDark,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.totalSize.ifBlank { "جاهز للتشغيل" },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("تشغيل كصوت في الخلفية") },
                        onClick = {
                            showMenu = false
                            onPlay()
                        },
                        leadingIcon = { Icon(Icons.Default.Headphones, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("نقل إلى الخزنة السرية") },
                        onClick = {
                            showMenu = false
                            Toast.makeText(context, "تم نقل الملف إلى الخزنة السرية بنجاح!", Toast.LENGTH_SHORT).show()
                        },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("حذف الملف") },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                    )
                }
            }
        }
    }
}
