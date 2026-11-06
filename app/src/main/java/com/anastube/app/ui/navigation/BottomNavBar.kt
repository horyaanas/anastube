package com.anastube.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class NavigationItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

@Composable
fun AnastubeBottomNavBar(
    currentRoute: String?,
    activeDownloadCount: Int = 0,
    onNavigate: (Screen) -> Unit
) {
    // SnapTube's exact 3 bottom navigation tabs
    val items = listOf(
        NavigationItem(Screen.Home, Icons.Default.FileDownload, "تنزيل"),
        NavigationItem(Screen.Downloads, Icons.Default.PlayCircleOutline, "تشغيل"),
        NavigationItem(Screen.Settings, Icons.Default.Settings, "الإعدادات")
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        items.forEach { item ->
            val isSelected = when (item.screen) {
                Screen.Home -> currentRoute == Screen.Home.route || currentRoute?.startsWith("browser") == true
                Screen.Downloads -> currentRoute == Screen.Downloads.route || currentRoute == Screen.Play.route || currentRoute == Screen.Library.route
                Screen.Settings -> currentRoute == Screen.Settings.route
                else -> currentRoute?.startsWith(item.screen.route.substringBefore("?")) == true
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    if (item.screen == Screen.Downloads && activeDownloadCount > 0) {
                        BadgedBox(badge = { Badge { Text("$activeDownloadCount") } }) {
                            Icon(imageVector = item.icon, contentDescription = item.label)
                        }
                    } else {
                        Icon(imageVector = item.icon, contentDescription = item.label)
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            )
        }
    }
}
