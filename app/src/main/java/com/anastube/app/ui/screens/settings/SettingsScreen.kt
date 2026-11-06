package com.anastube.app.ui.screens.settings

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anastube.app.ui.theme.SnapTubeYellow
import com.anastube.app.ui.theme.SnapTubeYellowDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var wifiOnly by remember { mutableStateOf(false) }
    var autoResume by remember { mutableStateOf(true) }
    var notificationEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(true) }
    var pipEnabled by remember { mutableStateOf(true) }
    var backgroundPlayEnabled by remember { mutableStateOf(true) }

    var concurrentTasks by remember { mutableStateOf(3) }
    var showTasksDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "الملف الشخصي والإعدادات",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp
                    )
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
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // VIP Profile Header Card (SnapTube VIP)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(54.dp),
                            shape = CircleShape,
                            color = SnapTubeYellow
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "مستخدم SnapTube",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = SnapTubeYellow
                                ) {
                                    Text(
                                        text = "VIP",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "عضوية VIP مفعلة • بدون إعلانات نهائياً • سرعة قصوى",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatItem(count = "24", label = "التنزيلات")
                        StatItem(count = "1.8 GB", label = "المساحة الموفرة")
                        StatItem(count = "عالية", label = "جودة الصوت")
                        StatItem(count = "4K", label = "أقصى دقة")
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Group 1: إعدادات التنزيل (Download Settings)
            SettingsGroupHeader(title = "إعدادات التنزيل")
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.CloudDownload,
                    title = "الحد الأقصى للمهام المتزامنة",
                    subtitle = "$concurrentTasks مهام في نفس الوقت",
                    onClick = { showTasksDialog = true }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.Folder,
                    title = "مسار حفظ الملفات",
                    subtitle = "/sdcard/Download/SnapTube",
                    onClick = {
                        Toast.makeText(context, "المسار الافتراضي: Download/SnapTube", Toast.LENGTH_SHORT).show()
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.Wifi,
                    title = "التنزيل عبر Wi-Fi فقط",
                    subtitle = if (wifiOnly) "مفعل (توفير بيانات الهاتف)" else "معطل (التنزيل عبر أي شبكة)",
                    trailing = {
                        Switch(
                            checked = wifiOnly,
                            onCheckedChange = { wifiOnly = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = SnapTubeYellow)
                        )
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.Sync,
                    title = "استئناف التنزيلات تلقائياً",
                    subtitle = "إعادة المحاولة تلقائياً عند انقطاع الإنترنت",
                    trailing = {
                        Switch(
                            checked = autoResume,
                            onCheckedChange = { autoResume = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = SnapTubeYellow)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Group 2: المظهر والمشغل (Theme & Player)
            SettingsGroupHeader(title = "المشغل والمظهر")
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.DarkMode,
                    title = "الوضع الداكن (SnapTube Dark Mode)",
                    subtitle = if (darkModeEnabled) "مفعل (خلفية داكنة مع لمسات صفراء)" else "الوضع الفاتح",
                    trailing = {
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = { darkModeEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = SnapTubeYellow)
                        )
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.PictureInPicture,
                    title = "نافذة عائمة (صورة داخل صورة - PiP)",
                    subtitle = "استمرار تشغيل الفيديو عند الخروج من التطبيق",
                    trailing = {
                        Switch(
                            checked = pipEnabled,
                            onCheckedChange = { pipEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = SnapTubeYellow)
                        )
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.Headphones,
                    title = "التشغيل في الخلفية",
                    subtitle = "تشغيل الصوت عند قفل الشاشة أو استخدام تطبيقات أخرى",
                    trailing = {
                        Switch(
                            checked = backgroundPlayEnabled,
                            onCheckedChange = { backgroundPlayEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = SnapTubeYellow)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Group 3: حول التطبيق (About)
            SettingsGroupHeader(title = "حول التطبيق")
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "إصدار SnapTube",
                    subtitle = "V7.25.1.7254510 (VIP النسخة الذهبية)",
                    onClick = {
                        Toast.makeText(context, "أنت تستخدم أحدث إصدار من SnapTube VIP!", Toast.LENGTH_SHORT).show()
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.CleaningServices,
                    title = "مسح الذاكرة المؤقتة (Cache)",
                    subtitle = "تحرير المساحة المؤقتة للصور والمصغرات",
                    onClick = {
                        Toast.makeText(context, "تم مسح الذاكرة المؤقتة بنجاح!", Toast.LENGTH_SHORT).show()
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.Share,
                    title = "مشاركة التطبيق مع الأصدقاء",
                    subtitle = "شارك SnapTube مع عائلتك وأصدقائك",
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "حمل تطبيق SnapTube الأصلي لتنزيل الفيديوهات والموسيقى بجودة عالية وبدون إعلانات!")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "مشاركة SnapTube"))
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showTasksDialog) {
        AlertDialog(
            onDismissRequest = { showTasksDialog = false },
            title = { Text("الحد الأقصى لمهام التنزيل المتزامنة") },
            text = {
                Column {
                    listOf(1, 2, 3, 5).forEach { num ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    concurrentTasks = num
                                    showTasksDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = concurrentTasks == num,
                                onClick = {
                                    concurrentTasks = num
                                    showTasksDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = SnapTubeYellow)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("$num مهام في نفس الوقت")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTasksDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = SnapTubeYellowDark
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun SettingsGroupHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = SnapTubeYellowDark,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        content = content
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = SnapTubeYellow.copy(alpha = 0.15f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SnapTubeYellowDark,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
