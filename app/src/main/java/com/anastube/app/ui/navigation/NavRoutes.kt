package com.anastube.app.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "الرئيسية")
    object Files : Screen("files", "ملفاتي")
    object Tools : Screen("tools", "الأدوات")
    object Me : Screen("me", "أنا")

    // Sub-screens & Overlays
    object Browser : Screen("browser?url={url}", "المتصفح") {
        fun createRoute(url: String) = "browser?url=$url"
    }

    object Player : Screen("player?url={url}&title={title}", "مشغل الوسائط") {
        fun createRoute(url: String, title: String) = "player?url=$url&title=$title"
    }

    object StatusSaver : Screen("status_saver", "حالات واتساب")
    object Vault : Screen("vault", "الخزنة السرية")
}
