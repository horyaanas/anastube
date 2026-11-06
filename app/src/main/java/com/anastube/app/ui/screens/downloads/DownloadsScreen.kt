package com.anastube.app.ui.screens.downloads

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anastube.app.data.DownloadItem
import com.anastube.app.data.DownloadState
import com.anastube.app.downloader.DownloadManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    onPlayLocalFile: (String, String) -> Unit
) {
    val context = LocalContext.current
    val downloads by DownloadManager.downloads.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var downloadedFilter by remember { mutableStateOf("All") }
    var showSpeedDialog by remember { mutableStateOf(false) }
    var selectedSpeedMode by remember { mutableStateOf("Maximum (Turbo)") }

    val activeDownloads = downloads.filter { it.state != DownloadState.COMPLETED }
    val completedDownloads = downloads.filter { it.state == DownloadState.COMPLETED }

    val filteredCompleted = when (downloadedFilter) {
        "Videos" -> completedDownloads.filter { !it.isAudio }
        "Music" -> completedDownloads.filter { it.isAudio }
        else -> completedDownloads
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Download Manager",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = { showSpeedDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Speed Control",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Storage Bar Indicator (SnapTube storage preview)
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Internal Storage: 38.4 GB Available",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Text(
                        text = selectedSpeedMode.take(7),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Downloading (${activeDownloads.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Downloaded (${completedDownloads.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            if (selectedTab == 0) {
                if (activeDownloads.isEmpty()) {
                    EmptyDownloadsView(
                        icon = Icons.Default.FileDownload,
                        title = "No active downloads",
                        subtitle = "Videos and music you download will appear here with live speed"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(activeDownloads) { item ->
                            ActiveDownloadRow(
                                item = item,
                                onPauseResume = {
                                    if (item.state == DownloadState.PAUSED) {
                                        DownloadManager.resumeDownload(context, item.id)
                                    } else {
                                        DownloadManager.pauseDownload(item.id)
                                    }
                                },
                                onCancel = {
                                    DownloadManager.cancelDownload(item.id)
                                }
                            )
                        }
                    }
                }
            } else {
                // Filter chips in Completed Tab
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Videos", "Music").forEach { filter ->
                        item {
                            FilterChip(
                                selected = downloadedFilter == filter,
                                onClick = { downloadedFilter = filter },
                                label = { Text(filter) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                if (filteredCompleted.isEmpty()) {
                    EmptyDownloadsView(
                        icon = Icons.Default.FolderOpen,
                        title = "No completed downloads yet",
                        subtitle = "Start downloading content to enjoy offline playback anytime"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredCompleted) { item ->
                            CompletedDownloadRow(
                                item = item,
                                onPlay = {
                                    onPlayLocalFile(item.destinationPath, item.title)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Speed Control Dialog (SnapTube dialog_adjust_speed_limit_layout)
    if (showSpeedDialog) {
        val speedOptions = listOf("Maximum (Turbo)", "Standard (5 MB/s)", "Battery Saver (1 MB/s)")
        AlertDialog(
            onDismissRequest = { showSpeedDialog = false },
            title = { Text("Download Speed Mode", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    speedOptions.forEach { opt ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSpeedMode = opt }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedSpeedMode == opt,
                                onClick = { selectedSpeedMode = opt },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(opt, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showSpeedDialog = false }) {
                    Text("Apply")
                }
            }
        )
    }
}

@Composable
fun ActiveDownloadRow(
    item: DownloadItem,
    onPauseResume: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (item.isAudio) Icons.Default.Audiotrack else Icons.Default.Videocam,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${item.format} (${item.quality}) • ${item.speed} • ${item.totalSize}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                IconButton(onClick = onPauseResume) {
                    Icon(
                        imageVector = if (item.state == DownloadState.PAUSED) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Pause/Resume",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = item.progress / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${item.progress}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.state.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun CompletedDownloadRow(
    item: DownloadItem,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (item.isAudio) Icons.Default.Audiotrack else Icons.Default.Videocam,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.format} • Completed • ${item.totalSize}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onPlay) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = { /* Share */ }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun EmptyDownloadsView(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                modifier = Modifier.size(88.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
