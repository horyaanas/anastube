package com.anastube.app.ui.screens.download

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anastube.app.data.FormatOption
import com.anastube.app.data.MediaItem
import com.anastube.app.ui.theme.SnapTubeYellow
import com.anastube.app.ui.theme.SnapTubeYellowDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadFormatSheet(
    mediaItem: MediaItem,
    formats: List<FormatOption>,
    onDismiss: () -> Unit,
    onStartDownload: (FormatOption, String) -> Unit
) {
    var selectedFormat by remember {
        mutableStateOf<FormatOption?>(formats.find { it.quality == "720p" } ?: formats.firstOrNull())
    }
    var fileName by remember { mutableStateOf(mediaItem.title) }
    var isEditingName by remember { mutableStateOf(false) }
    var fastDownloadAccelerated by remember { mutableStateOf(true) }

    val audioFormats = formats.filter { it.isAudioOnly }
    val videoFormats = formats.filter { !it.isAudioOnly }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp),
                shape = RoundedCornerShape(2.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            // Header Title: SnapTube Choose Format Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تنزيل الفيديو بصيغة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SnapTubeYellow.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = SnapTubeYellowDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SnapTube VIP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SnapTubeYellowDark
                        )
                    }
                }
            }

            // Media Info Card: Thumbnail + Editable Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = mediaItem.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 84.dp, height = 54.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    if (isEditingName) {
                        OutlinedTextField(
                            value = fileName,
                            onValueChange = { fileName = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = fileName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Text(
                            text = "${mediaItem.author} • ${mediaItem.duration}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = { isEditingName = !isEditingName }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Rename",
                        tint = SnapTubeYellowDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.surfaceVariant)

            // Scrollable Format Options
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Section 1: Music / Audio (SnapTube MUSIC section)
                if (audioFormats.isNotEmpty()) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Audiotrack,
                                contentDescription = null,
                                tint = SnapTubeYellowDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "موسيقى (MUSIC)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    items(audioFormats.size) { index ->
                        val format = audioFormats[index]
                        FormatItemCard(
                            format = format,
                            isSelected = selectedFormat?.id == format.id,
                            onClick = { selectedFormat = format }
                        )
                    }
                }

                // Section 2: Video (SnapTube VIDEO section)
                if (videoFormats.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = SnapTubeYellowDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "فيديو (VIDEO)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    items(videoFormats.size) { index ->
                        val format = videoFormats[index]
                        FormatItemCard(
                            format = format,
                            isSelected = selectedFormat?.id == format.id,
                            onClick = { selectedFormat = format }
                        )
                    }
                }
            }

            // SnapTube Acceleration Toggle Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = SnapTubeYellowDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "محرك تسريع التحميل المتعدد (Multi-Thread VIP)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Switch(
                    checked = fastDownloadAccelerated,
                    onCheckedChange = { fastDownloadAccelerated = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = SnapTubeYellow
                    )
                )
            }

            // Big Yellow Download Button (SnapTube Signature CTA)
            Button(
                onClick = {
                    selectedFormat?.let { fmt ->
                        onStartDownload(fmt, fileName)
                    }
                },
                enabled = selectedFormat != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(top = 4.dp, bottom = 6.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SnapTubeYellow,
                    contentColor = Color.Black
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تنزيل (${selectedFormat?.format ?: ""} • ${selectedFormat?.estimatedSize ?: ""})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun FormatItemCard(
    format: FormatOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) SnapTubeYellow else MaterialTheme.colorScheme.surfaceVariant
    val bgColor = if (isSelected) SnapTubeYellow.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(width = if (isSelected) 1.5.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp)),
        color = bgColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Quality & Resolution Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) SnapTubeYellow else MaterialTheme.colorScheme.surface,
                    modifier = Modifier.defaultMinSize(minWidth = 52.dp)
                ) {
                    Text(
                        text = format.format,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = format.label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = format.bitrate,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = format.estimatedSize,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isSelected) SnapTubeYellowDark else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) SnapTubeYellow else Color.Transparent,
                    border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)) else null,
                    modifier = Modifier.size(22.dp)
                ) {
                    if (isSelected) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
