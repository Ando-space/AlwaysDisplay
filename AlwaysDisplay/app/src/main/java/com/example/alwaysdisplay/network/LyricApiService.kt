package com.example.alwaysdisplay.network

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * 在线歌词API服务（LRCLIB）
 */
class LyricApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    data class LyricResult(
        val id: Long = 0,
        val name: String = "",
        val trackName: String = "",
        val artistName: String = "",
        @SerializedName("syncedLyrics")
        val syncedLyrics: String? = null,
        @SerializedName("plainLyrics")
        val plainLyrics: String? = null
    )

    suspend fun searchLyrics(query: String): LyricResult? = withContext(Dispatchers.IO) {
        try {
            val url = "https://lrclib.net/api/search?q=${java.net.URLEncoder.encode(query, "UTF-8")}"
            val request = Request.Builder().url(url).build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val body = response.body?.string() ?: return@withContext null
            val results = gson.fromJson(body, Array<LyricResult>::class.java)

            // 优先返回有同步歌词的结果
            results?.firstOrNull { !it.syncedLyrics.isNullOrBlank() }
                ?: results?.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }
}
