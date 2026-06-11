package com.ezDrop.app.data.skin

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

private const val TAG = "SkinRepo"

object SkinImageRepository {

    private const val API_URL = "https://raw.githubusercontent.com/ByMykel/CSGO-API/main/public/api/en/skins_not_grouped.json"
    private const val CACHE_FILENAME = "skin_image_cache.json"
    private const val STALE_DAYS = 7L

    private val wearOrder = listOf(
        "Factory New", "Minimal Wear", "Field-Tested", "Well-Worn", "Battle-Scarred"
    )

    private val builtinFallbacks = mapOf(
        "Karambit | Fade" to mapOf(
            "Factory New" to "https://community.akamai.steamstatic.com/economy/image/i0CoZ81Ui0m-9KwlBY1L_18myuGuq1wfhWSaZgMttyVfPaERSR0Wqmu7LAocGIGz3UqlXOLrxM-vMGmW8VNxu5Dx60noTyL6kJ_m-B1Q7uCvZaZkNM-SD1iWwOpzj-1gSCGn20tztm_UyIn_JHKUbgYlWMcmQ-ZcskSwldS0MOnntAfd3YlMzH35jntXrnE8SOGRGG8"
        )
    )

    private val json = Json { ignoreUnknownKeys = true }
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val _urlCache = MutableStateFlow<Map<String, String>>(emptyMap())
    val urlCache: StateFlow<Map<String, String>> = _urlCache.asStateFlow()

    private var loaded = false

    private val knownItems = setOf(
        "Glock-18 | Sand Dune",
        "P250 | Boreal Forest",
        "SSG 08 | Jungle Dashed",
        "FAMAS | Colony",
        "MP5-SD | Jungle Slipstream",
        "MP9 | Rose Iron",
        "MAC-10 | Silver",
        "P2000 | Ivory",
        "UMP-45 | Carbon Fiber",
        "M249 | Magma",
        "AK-47 | Redline",
        "M4A1-S | Blood Tiger",
        "AWP | Worm God",
        "USP-S | Guardian",
        "M4A4 | Asiimov",
        "AWP | Hyper Beast",
        "Desert Eagle | Blaze",
        "AWP | Dragon Lore",
        "Karambit | Fade",
    )

    fun isLoaded(): Boolean = loaded

    suspend fun ensureLoaded(context: Context) {
        if (loaded) {
            Log.d(TAG, "Already loaded, skipping")
            return
        }
        withContext(Dispatchers.IO) {
            val cacheFile = File(context.filesDir, CACHE_FILENAME)
            Log.d(TAG, "Cache exists=${cacheFile.exists()}, path=${cacheFile.absolutePath}")
            if (cacheFile.exists() && !isStale(cacheFile)) {
                Log.d(TAG, "Loading from cache")
                loadFromCache(cacheFile)
            } else {
                Log.d(TAG, "Cache missing or stale, fetching from API")
                fetchAndCache(context)
            }
            loaded = true
            Log.d(TAG, "Loaded, cache size=${_urlCache.value.size}")
        }
    }

    fun getUrlByName(name: String, wearTier: String): String? {
        val marketName = "$name ($wearTier)"
        return _urlCache.value[marketName]
    }

    private fun isStale(file: File): Boolean {
        val age = System.currentTimeMillis() - file.lastModified()
        return age > STALE_DAYS * 24 * 60 * 60 * 1000
    }

    private fun buildExpandedCache(raw: Map<String, String>): Map<String, String> {
        val grouped = mutableMapOf<String, MutableMap<String, String>>()

        for ((key, url) in raw) {
            val baseName = key.substringBeforeLast(" (")
            val wear = key.substringAfterLast(" (", "").substringBefore(")")
            grouped.getOrPut(baseName) { mutableMapOf() }[wear] = url
        }

        for ((baseName, wears) in builtinFallbacks) {
            val existing = grouped.getOrPut(baseName) { mutableMapOf() }
            for ((wear, url) in wears) {
                if (wear !in existing) {
                    existing[wear] = url
                }
            }
        }

        val expanded = mutableMapOf<String, String>()
        for ((baseName, wears) in grouped) {
            for (wear in wearOrder) {
                val url = wears[wear] ?: fallbackWear(wears, wear)
                if (url != null) {
                    expanded["$baseName ($wear)"] = url
                }
            }
        }

        return expanded
    }

    private fun fallbackWear(wears: Map<String, String>, requested: String): String? {
        val idx = wearOrder.indexOf(requested)
        if (idx < 0) return null
        for (offset in 1..4) {
            for (dir in listOf(1, -1)) {
                val i = idx + offset * dir
                if (i in wearOrder.indices) {
                    val url = wears[wearOrder[i]]
                    if (url != null) return url
                }
            }
        }
        return null
    }

    private fun loadFromCache(file: File) {
        try {
            val text = file.readText()
            Log.d(TAG, "Cache file size: ${text.length} bytes")
            val entries = json.decodeFromString<List<CacheEntry>>(text)
            Log.d(TAG, "Loaded ${entries.size} entries from cache")
            _urlCache.value = buildExpandedCache(entries.associate { it.key to it.url })
            Log.d(TAG, "Cache populated, keys: ${_urlCache.value.keys.take(3)}...")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load cache", e)
        }
    }

    private suspend fun fetchAndCache(context: Context) {
        try {
            Log.d(TAG, "Starting API fetch...")
            val request = Request.Builder().url(API_URL).build()
            val response = httpClient.newCall(request).execute()
            Log.d(TAG, "HTTP ${response.code} ${response.message}")
            if (!response.isSuccessful) throw Exception("HTTP ${response.code}")

            val body = response.body!!.string()
            Log.d(TAG, "Downloaded ${body.length} bytes")

            val allSkins = json.decodeFromString<List<ApiSkin>>(body)
            Log.d(TAG, "Parsed ${allSkins.size} skins from JSON")

            val cacheEntries = allSkins
                .filter { skin ->
                    knownItems.any { known ->
                        skin.marketHashName.startsWith("$known (") || skin.marketHashName == known
                    }
                }
                .map { skin ->
                    CacheEntry(key = skin.marketHashName, url = skin.image)
                }

            Log.d(TAG, "Filtered to ${cacheEntries.size} entries for our items")
            if (cacheEntries.isNotEmpty()) {
                Log.d(TAG, "Sample: ${cacheEntries.first().key} -> ${cacheEntries.first().url.take(60)}...")
            }

            _urlCache.value = buildExpandedCache(cacheEntries.associate { it.key to it.url })

            val cacheFile = File(context.filesDir, CACHE_FILENAME)
            cacheFile.writeText(json.encodeToString(cacheEntries))
            Log.d(TAG, "Cache written to ${cacheFile.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Fetch failed", e)
        }
    }
}

@Serializable
private data class CacheEntry(
    val key: String,
    val url: String,
)
