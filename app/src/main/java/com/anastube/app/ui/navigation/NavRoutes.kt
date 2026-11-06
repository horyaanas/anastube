package com.anastube.app.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Browser : Screen("browser?url={url}", "Browser") {
        fun createRoute(url: String) = "browser?url=$url"
    }
    object Downloads : Screen("downloads", "Downloads")
    object Library : Screen("library", "My Files")
    object Settings : Screen("settings", "Settings")
    object Player : Screen("player?url={url}&title={title}", "Player") {
        fun createRoute(url: String, title: String) = "player?url=$url&title=$title"
    }
}
