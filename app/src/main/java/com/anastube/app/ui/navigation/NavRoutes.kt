package com.anastube.app.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "تنزيل")
    object Play : Screen("play", "تشغيل")
    object Downloads : Screen("downloads", "تم التنزيل")
    object Library : Screen("library", "الملفات")
    object Settings : Screen("settings", "الإعدادات")

    object Browser : Screen("browser?url={url}", "المتصفح") {
        fun createRoute(url: String) = "browser?url=$url"
    }

    object Player : Screen("player?url={url}&title={title}", "مشغل الوسائط") {
        fun createRoute(url: String, title: String) = "player?url=$url&title=$title"
    }
}
