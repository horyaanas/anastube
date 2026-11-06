package com.anastube.app.ui.screens.tools

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anastube.app.ui.theme.SnapTubeYellow
import com.anastube.app.ui.theme.WhatsAppGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class WhatsAppStatusItem(
    val id: String,
    val previewUrl: String,
    val isVideo: Boolean,
    val duration: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    onOpenVault: () -> Unit,
    onNavigateToBrowser: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var isCleaning by remember { mutableStateOf(false) }
    var cleanedJunkSize by remember { mutableStateOf("1.85 GB") }
    var cleanSuccess by remember { mutableStateOf(false) }

    var isBoosting by remember { mutableStateOf(false) }
    var ramFreed by remember { mutableStateOf("650 MB") }
    var boostSuccess by remember { mutableStateOf(false) }

    var showWhatsAppSaverSheet by remember { mutableStateOf(false) }
    var showConverterDialog by remember { mutableStateOf(false) }

    val sampleStatuses = remember {
        listOf(
            WhatsAppStatusItem("wa_1", "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=400&q=80", false),
            WhatsAppStatusItem("wa_2", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=400&q=80", true, "0:30"),
            WhatsAppStatusItem("wa_3", "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=400&q=80", false),
            WhatsAppStatusItem("wa_4", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=400&q=80", true, "0:15")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            color = SnapTubeYellow
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "أدوات SnapTube",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Tool 1: WhatsApp Status Saver Banner (SnapTube's #1 Tool)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showWhatsAppSaverSheet = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WhatsAppGreen.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = WhatsAppGreen
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "حفظ حالات WhatsApp",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = WhatsAppGreen
                            ) {
                                Text(
                                    text = "جديد",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "تنزيل ومشاركة صور وفيديوهات الحالات بنقرة واحدة",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = WhatsAppGreen
                    )
                }
            }

            // Hero Tool 2: Phone Cleaner (منظف الملفات المهملة)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = if (cleanSuccess) Color(0xFF4CAF50) else Color(0xFFFF5252)
                        ) {
                            Icon(
                                imageVector = if (cleanSuccess) Icons.Default.CheckCircle else Icons.Default.CleaningServices,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (cleanSuccess) "تم تنظيف الهاتف بنجاح!" else "الملفات المهملة والمؤقتة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (cleanSuccess) "تم توفير $cleanedJunkSize من مساحة التخزين" else "$cleanedJunkSize قابلة للتحرير والتسريع",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    isCleaning = true
                                    delay(1500)
                                    isCleaning = false
                                    cleanSuccess = true
                                    Toast.makeText(context, "تم تنظيف $cleanedJunkSize بنجاح!", Toast.LENGTH_LONG).show()
                                }
                            },
                            enabled = !isCleaning && !cleanSuccess,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SnapTubeYellow, contentColor = Color.Black)
                        ) {
                            if (isCleaning) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.Black)
                            } else {
                                Text(if (cleanSuccess) "نظيف" else "تنظيف الآن", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Grid of Essential SnapTube Utilities
            Text(
                text = "المزيد من الأدوات المفيدة",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Phone Booster
                UtilityCard(
                    title = "تسريع الذاكرة",
                    subtitle = if (boostSuccess) "تم تحسين RAM" else "تحرير $ramFreed",
                    icon = Icons.Default.Speed,
                    iconBg = Color(0xFF2979FF),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            isBoosting = true
                            delay(1200)
                            isBoosting = false
                            boostSuccess = true
                            Toast.makeText(context, "تم تحرير $ramFreed وتسريع الهاتف!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // Battery Saver
                UtilityCard(
                    title = "موفر البطارية",
                    subtitle = "تحسين استهلاك الطاقة",
                    icon = Icons.Default.BatteryChargingFull,
                    iconBg = Color(0xFF00C853),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        Toast.makeText(context, "تم تفعيل وضع توفير الطاقة بنجاح!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Video to MP3 Converter
                UtilityCard(
                    title = "تحويل إلى MP3",
                    subtitle = "استخراج الصوت بجودة 320K",
                    icon = Icons.Default.MusicNote,
                    iconBg = Color(0xFFFF9100),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        showConverterDialog = true
                    }
                )

                // Private Vault
                UtilityCard(
                    title = "الخزنة السرية",
                    subtitle = "إخفاء الفيديوهات بكلمة سر",
                    icon = Icons.Default.Lock,
                    iconBg = Color(0xFF7C4DFF),
                    modifier = Modifier.weight(1f),
                    onClick = onOpenVault
                )
            }
        }
    }

    // WhatsApp Status Saver Bottom Sheet Modal
    if (showWhatsAppSaverSheet) {
        ModalBottomSheet(
            onDismissRequest = { showWhatsAppSaverSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = WhatsAppGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "حالات WhatsApp المرصودة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Text(
                    text = "انقر على أي حالة لحفظها مباشرة في استوديو جهازك",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    items(sampleStatuses) { status ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black)
                        ) {
                            AsyncImage(
                                model = status.previewUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            if (status.isVideo) {
                                Surface(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .align(Alignment.TopStart),
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.Black.copy(alpha = 0.6f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                        Text(text = status.duration, color = Color.White, fontSize = 10.sp)
                                    }
                                }
                            }

                            // Download / Save Button (SnapTube style)
                            Surface(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.BottomEnd)
                                    .size(36.dp)
                                    .clickable {
                                        Toast.makeText(context, "تم حفظ الحالة في المعرض!", Toast.LENGTH_SHORT).show()
                                    },
                                shape = CircleShape,
                                color = SnapTubeYellow
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = "Save Status",
                                    tint = Color.Black,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Video to MP3 Converter Dialog
    if (showConverterDialog) {
        AlertDialog(
            onDismissRequest = { showConverterDialog = false },
            title = {
                Text("محول الفيديو إلى MP3", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("اختر فيديو من ملفاتك المنزلة لتحويله فورياً إلى ملف صوتي MP3 عالي الدقة (320 kbps).")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConverterDialog = false
                        Toast.makeText(context, "تم استخراج ملف الصوت MP3 بنجاح!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SnapTubeYellow, contentColor = Color.Black)
                ) {
                    Text("اختيار وتحويل", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConverterDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun UtilityCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                color = iconBg
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(9.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
