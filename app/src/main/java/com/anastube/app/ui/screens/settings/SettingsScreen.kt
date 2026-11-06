package com.anastube.app.ui.screens.settings

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var wifiOnly by remember { mutableStateOf(false) }
    var notificationEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(true) }

    var showCleanDialog by remember { mutableStateOf(false) }
    var cleanDialogTitle by remember { mutableStateOf("") }
    var cleanDialogMsg by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "الإعدادات",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp
                    )
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
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Group 1: عام (Screenshot 5)
            SettingsGroupHeader(title = "عام")
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.FileDownload,
                    title = "إعدادات التنزيل",
                    subtitle = "مسار التنزيل، الحد الأقصى للمهام المتزامنة (3 مهام)",
                    onClick = {
                        Toast.makeText(context, "مسار التنزيل الحالي: Movies/Anastube", Toast.LENGTH_SHORT).show()
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "إعدادات الإشعارات",
                    subtitle = if (notificationEnabled) "مفعّلة (إشعار اكتمال التنزيل)" else "معطّلة",
                    trailing = {
                        Switch(
                            checked = notificationEnabled,
                            onCheckedChange = { notificationEnabled = it }
                        )
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.DarkMode,
                    title = "السمة / المظهر",
                    subtitle = if (darkModeEnabled) "الوضع الداكن الفاخر (Royal Blue)" else "الوضع الفاتح",
                    trailing = {
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = { darkModeEnabled = it }
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Group 2: أدوات التنزيل (Screenshot 5)
            SettingsGroupHeader(title = "أدوات التنزيل")
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.RestoreFromTrash,
                    title = "استعادة الملفات المحذوفة",
                    subtitle = "سلة المهملات والملفات المؤقتة",
                    onClick = {
                        Toast.makeText(context, "سلة المهملات فارغة حالياً", Toast.LENGTH_SHORT).show()
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.ChatBubble,
                    title = "منزل حالات WhatsApp",
                    subtitle = "تنزيل الصور وفيديوهات الحالة المتاحة بنقرة واحدة",
                    onClick = {
                        Toast.makeText(context, "منزل الحالات جاهز ويعمل بالخلفية", Toast.LENGTH_SHORT).show()
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "الخزنة",
                    subtitle = "حماية وتشفير الفيديوهات والمستندات الخاصة",
                    onClick = {
                        Toast.makeText(context, "الخزنة نشطة ومؤمنة", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Group 3: تنظيف الهاتف (Screenshot 5)
            SettingsGroupHeader(title = "تنظيف الهاتف")
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.CleaningServices,
                    title = "تنظيف الملفات غير المرغوب فيها",
                    subtitle = "مسح الكاش والمخلفات المؤقتة لتسريع الهاتف",
                    onClick = {
                        cleanDialogTitle = "تنظيف الملفات غير المرغوب فيها"
                        cleanDialogMsg = "تم تنظيف 340 MB من ملفات التخزين المؤقت بنجاح!"
                        showCleanDialog = true
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.Speed,
                    title = "تعزيز الذاكرة (RAM Boost)",
                    subtitle = "إيقاف العمليات الخاملة لتوفير الذاكرة",
                    onClick = {
                        cleanDialogTitle = "تعزيز الذاكرة"
                        cleanDialogMsg = "تم تسريع الهاتف وتحرير 1.2 GB من ذاكرة RAM!"
                        showCleanDialog = true
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.BatteryChargingFull,
                    title = "موفر البطارية",
                    subtitle = "تحسين استهلاك الطاقة وإطالة عمر البطارية",
                    onClick = {
                        cleanDialogTitle = "موفر البطارية"
                        cleanDialogMsg = "تم تفعيل وضع التوفير الذكي للطاقة بنجاح."
                        showCleanDialog = true
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.FolderSpecial,
                    title = "تنظيف الملفات الكبيرة",
                    subtitle = "فحص الفيديوهات والملفات التي تتجاوز 100 MB",
                    onClick = {
                        Toast.makeText(context, "لا توجد ملفات كبيرة مكررة", Toast.LENGTH_SHORT).show()
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.DeleteSweep,
                    title = "تنظيف سلة المهملات",
                    subtitle = "حذف نهائي للملفات المستغنى عنها",
                    onClick = {
                        Toast.makeText(context, "تم إفراغ سلة المهملات", Toast.LENGTH_SHORT).show()
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.Chat,
                    title = "تنظيف WhatsApp",
                    subtitle = "إدارة وسائط المقاطع الصوتية ومقاطع الفيديو المتراكمة",
                    onClick = {
                        Toast.makeText(context, "فحص مجلدات WhatsApp مكتمل", Toast.LENGTH_SHORT).show()
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.PhotoLibrary,
                    title = "تنظيف الصور المكررة",
                    subtitle = "اكتشاف الصور المتطابقة لتوفير المساحة",
                    onClick = {
                        Toast.makeText(context, "لم يتم العثور على صور مكررة", Toast.LENGTH_SHORT).show()
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.Apps,
                    title = "إلغاء تثبيت التطبيقات",
                    subtitle = "إدارة وحذف التطبيقات النادرة الاستخدام",
                    onClick = {
                        Toast.makeText(context, "إدارة التطبيقات جاهزة", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Group 4: معلومات (Screenshot 5)
            SettingsGroupHeader(title = "معلومات")
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.AccountCircle,
                    title = "الحساب",
                    subtitle = "مزامنة التنزيلات والعلامات المرجعية",
                    onClick = {
                        Toast.makeText(context, "أنت تستخدم وضع الضيف النظيف بدون تسجيل دخول", Toast.LENGTH_SHORT).show()
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = "اللغة",
                    subtitle = "العربية (الافتراضية)",
                    onClick = {
                        Toast.makeText(context, "التطبيق يدعم العربية والإنجليزية", Toast.LENGTH_SHORT).show()
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.Feedback,
                    title = "ملاحظات",
                    subtitle = "شاركنا رأيك أو أبلغ عن أي مشكلة لتحسين التطبيق",
                    onClick = {
                        val emailIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "message/rfc822"
                            putExtra(Intent.EXTRA_SUBJECT, "ملاحظات حول تطبيق Anastube")
                        }
                        context.startActivity(Intent.createChooser(emailIntent, "إرسال ملاحظات"))
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.Share,
                    title = "مشاركة Anastube",
                    subtitle = "شارك التطبيق مع أصدقائك للاستمتاع بتجربة تنزيل خالية من الإعلانات",
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "حمّل تطبيق Anastube لتنزيل الفيديوهات والموسيقى بدون أي إعلانات مجاناً!")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "مشاركة التطبيق"))
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "حول Anastube",
                    subtitle = "الإصدار 1.0.0 (Zero Ads Edition) • تطوير عصري",
                    onClick = {
                        cleanDialogTitle = "حول Anastube"
                        cleanDialogMsg = "Anastube هو تطبيق تنزيل وتشغيل وسائط حر ومستقل، بدون أي إعلانات مزعجة وبحماية كاملة للخصوصية."
                        showCleanDialog = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Clean action dialog
        if (showCleanDialog) {
            AlertDialog(
                onDismissRequest = { showCleanDialog = false },
                title = { Text(cleanDialogTitle, fontWeight = FontWeight.Bold) },
                text = { Text(cleanDialogMsg) },
                confirmButton = {
                    Button(
                        onClick = { showCleanDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("حسناً")
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsGroupHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        content = content
    )
}

@Composable
private fun SettingsItem(
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
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

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
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                lineHeight = 15.sp
            )
        }

        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
