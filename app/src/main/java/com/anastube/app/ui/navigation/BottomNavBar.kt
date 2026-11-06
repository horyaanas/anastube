package com.anastube.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
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
    val items = listOf(
        NavigationItem(Screen.Home, Icons.Default.Home, "Home"),
        NavigationItem(Screen.Browser, Icons.Default.Language, "Browser"),
        NavigationItem(Screen.Downloads, Icons.Default.Download, "Downloads"),
        NavigationItem(Screen.Library, Icons.Default.Folder, "Files"),
        NavigationItem(Screen.Settings, Icons.Default.Settings, "Settings")
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        items.forEach { item ->
            val isSelected = currentRoute?.startsWith(item.screen.route.substringBefore("?")) == true

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
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            )
        }
    }
}
