package com.anastube.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.anastube.app.ui.theme.SnapTubeYellow

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
    // SnapTube's 4 core navigation tabs
    val items = listOf(
        NavigationItem(Screen.Home, Icons.Default.Home, "الرئيسية"),
        NavigationItem(Screen.Files, Icons.Default.FileDownload, "ملفاتي"),
        NavigationItem(Screen.Tools, Icons.Default.Build, "الأدوات"),
        NavigationItem(Screen.Me, Icons.Default.Person, "أنا")
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        items.forEach { item ->
            val isSelected = when (item.screen) {
                Screen.Home -> currentRoute == Screen.Home.route || currentRoute?.startsWith("browser") == true
                Screen.Files -> currentRoute == Screen.Files.route
                Screen.Tools -> currentRoute == Screen.Tools.route || currentRoute == Screen.StatusSaver.route || currentRoute == Screen.Vault.route
                Screen.Me -> currentRoute == Screen.Me.route
                else -> currentRoute == item.screen.route
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    if (item.screen == Screen.Files && activeDownloadCount > 0) {
                        BadgedBox(badge = {
                            Badge(
                                containerColor = SnapTubeYellow,
                                contentColor = Color.Black
                            ) {
                                Text("$activeDownloadCount", fontWeight = FontWeight.Bold)
                            }
                        }) {
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
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SnapTubeYellow,
                    selectedTextColor = SnapTubeYellow,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = SnapTubeYellow.copy(alpha = 0.15f)
                )
            )
        }
    }
}
