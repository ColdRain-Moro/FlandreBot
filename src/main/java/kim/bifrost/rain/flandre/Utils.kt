package kim.bifrost.rain.flandre

import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * kim.bifrost.rain.flandre.Utils
 * Flandre
 *
 * @author 寒雨
 * @since 2022/3/13 13:42
 **/
val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .build()

val gson = Gson()

suspend fun getImage(url: String): ByteArray? = suspendCoroutine {
    val request = Request.Builder()
        .url(url)
        .addHeader("Referer", "https://pixiviz.pwp.app/")
        .build()
    client.newCall(request)
        .enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                it.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                it.resume(response.body?.bytes())
            }

        })
}