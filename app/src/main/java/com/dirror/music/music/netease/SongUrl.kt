package com.dirror.music.music.netease

import androidx.core.location.LocationRequestCompat.Quality
import com.dirror.music.App.Companion.mmkv
import com.dirror.music.api.API_AUTU
import com.dirror.music.manager.User
import com.dirror.music.music.dirror.SearchSong
import com.dirror.music.music.netease.data.SongUrlData
import com.dirror.music.util.AppConfig
import com.dirror.music.util.Config
import com.dirror.music.util.HttpUtils
import com.dirror.music.util.MagicHttp
import com.google.gson.Gson
import okhttp3.FormBody

object SongUrl {

    const val API = "${API_AUTU}/song/url?id=33894312"

    fun getSongUrl(id: String): String {
        return if (SearchSong.getDirrorSongUrl(id) != "") {
            SearchSong.getDirrorSongUrl(id)
        } else {
            "https://music.163.com/song/media/outer/url?id=${id}.mp3"
        }
    }

    fun getSongUrlCookie(id: String, success: (String) -> Unit) {
        var api = User.neteaseCloudMusicApi
        if (api.isEmpty()) {
            api = "https://papi.aymao.com"
        }
        var quality = mmkv.decodeString(Config.Quality,"标准")!!
        val qualityMapping = mapOf(
            "标准" to "standard",
            "较高" to "higher",
            "极高" to "exhigh",
            "无损" to "lossless",
            "Hi-Res" to "hires",
            "高清环绕" to "jyeffect",
            "沉浸环绕" to "sky",
            "杜比全景" to "dolby",
            "母带级" to "jymaster"
        )
        quality = qualityMapping[quality] ?: quality
        val requestBody = FormBody.Builder()
            .add("crypto", "api")
            .add("level", quality)
            .add("withCredentials", "true")
            .add("realIP", "211.161.244.70")
            .add("id", id)
            .build()
        MagicHttp.OkHttpManager().newPost("${api}/song/url/v1", requestBody, {
            try {
                val songUrlData = Gson().fromJson(it, SongUrlData::class.java)
                success.invoke(songUrlData.data[0].url ?: "")
            } catch (e: Exception) {
                // failure.invoke(ErrorCode.ERROR_JSON)
            }
        }, {

        })
    }

    suspend fun getSongUrlN(id: String): String {
        val quality = mmkv.decodeString(Config.Quality)
        val url = "$API_AUTU/song/url/v1?id=$id&level=$quality"
        val result = HttpUtils.get(url, SongUrlData::class.java)
        return result?.data?.get(0)?.url ?: getSongUrl(id)
    }

}