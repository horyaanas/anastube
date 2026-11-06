package com.anastube.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object YouTubeRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    suspend fun searchVideos(query: String): List<MediaItem> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = "https://www.youtube.com/results?search_query=$encodedQuery"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build()

            val response = client.newCall(request).execute()
            val html = response.body?.string() ?: return@withContext emptyList()
            parseYtInitialData(html)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getTrendingVideos(): List<MediaItem> = withContext(Dispatchers.IO) {
        try {
            val url = "https://www.youtube.com/feed/trending"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build()

            val response = client.newCall(request).execute()
            val html = response.body?.string() ?: return@withContext emptyList()
            val list = parseYtInitialData(html)
            if (list.isNotEmpty()) list else searchVideos("Trending music hits 2026")
        } catch (e: Exception) {
            e.printStackTrace()
            searchVideos("Trending music hits 2026")
        }
    }

    private fun parseYtInitialData(html: String): List<MediaItem> {
        val pattern = Pattern.compile("ytInitialData\\s*=\\s*(\\{.+?\\});</script>", Pattern.DOTALL)
        val matcher = pattern.matcher(html)
        if (!matcher.find()) return emptyList()

        val jsonStr = matcher.group(1) ?: return emptyList()
        val results = mutableListOf<MediaItem>()

        try {
            val root = JSONObject(jsonStr)
            val contents = root.optJSONObject("contents")
                ?.optJSONObject("twoColumnSearchResultsRenderer")
                ?.optJSONObject("primaryContents")
                ?.optJSONObject("sectionListRenderer")
                ?.optJSONArray("contents")

            if (contents != null) {
                for (i in 0 until contents.length()) {
                    val section = contents.optJSONObject(i)
                    val itemSection = section?.optJSONObject("itemSectionRenderer")?.optJSONArray("contents") ?: continue
                    for (j in 0 until itemSection.length()) {
                        val item = itemSection.optJSONObject(j) ?: continue
                        val vr = item.optJSONObject("videoRenderer") ?: continue
                        val media = parseVideoRenderer(vr)
                        if (media != null) results.add(media)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return results
    }

    private fun parseVideoRenderer(vr: JSONObject): MediaItem? {
        try {
            val videoId = vr.optString("videoId")
            if (videoId.length != 11) return null

            val titleRuns = vr.optJSONObject("title")?.optJSONArray("runs")
            val title = titleRuns?.optJSONObject(0)?.optString("text") ?: "YouTube Video"

            val ownerRuns = vr.optJSONObject("ownerText")?.optJSONArray("runs")
            val channel = ownerRuns?.optJSONObject(0)?.optString("text") ?: "YouTube Channel"

            val duration = vr.optJSONObject("lengthText")?.optString("simpleText") ?: "HD"
            val views = vr.optJSONObject("viewCountText")?.optString("simpleText") ?: ""

            val thumbArray = vr.optJSONObject("thumbnail")?.optJSONArray("thumbnails")
            val thumbUrl = if (thumbArray != null && thumbArray.length() > 0) {
                thumbArray.optJSONObject(thumbArray.length() - 1)?.optString("url") ?: "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
            } else {
                "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
            }

            return MediaItem(
                id = videoId,
                title = title,
                url = "https://www.youtube.com/watch?v=$videoId",
                thumbnailUrl = thumbUrl,
                duration = duration,
                author = channel,
                views = views
            )
        } catch (e: Exception) {
            return null
        }
    }
}
