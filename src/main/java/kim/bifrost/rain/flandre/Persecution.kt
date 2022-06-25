package kim.bifrost.rain.flandre

import com.google.gson.reflect.TypeToken
import kim.bifrost.rain.flandre.data.persecution.BaseResponse
import kim.bifrost.rain.flandre.data.persecution.ClassificationData
import kim.bifrost.rain.flandre.data.persecution.Pager
import kim.bifrost.rain.flandre.data.persecution.SingleImageData
import kotlinx.coroutines.CoroutineScope
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.util.safeCast
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.closeQuietly
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * kim.bifrost.rain.flandre.Persecution
 * Flandre
 * 迫害！
 *
 * @author 寒雨
 * @since 2022/3/4 13:21
 **/
object Persecution : CoroutineScope by Flandre {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    fun init() {
        GlobalEventChannel.parentScope(this).subscribeAlways<GroupMessageEvent> {
            if (message.content.startsWith("迫害 ")) {
                val key = message.content.substring(3)
                val classification = getClassification(key) ?: return@subscribeAlways let {
                    group.sendMessage(message.quote().plus("不存在该分类"))
                }
                val image = getRandomImage(classification.id, getClassificationSize(classification.id)) ?: return@subscribeAlways let {
                    group.sendMessage(message.quote().plus("该分类没有图片"))
                }
                val img = getImage(image.image)?.toExternalResource() ?: return@subscribeAlways let {
                    group.sendMessage("内部错误: 不存在的图片")
                }
                val imageId = img.uploadAsImage(group).imageId
                img.closeQuietly()
                group.sendMessage(Image(imageId))
            }
        }
    }

    private suspend fun getClassification(classification: String): ClassificationData? = suspendCoroutine {
        client.newCall(
            Request.Builder()
                .url("http://42.192.196.215:8080/classification?name=${classification}")
                .build()
        ).enqueue(
            object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    it.resumeWithException(e)
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val body = response.body!!.string()
                    val data = gson.fromJson<BaseResponse<ClassificationData>>(body, object : TypeToken<BaseResponse<ClassificationData>>() {}.type)
                    it.resume(data.data)
                }
            }
        )
    }

    private suspend fun getRandomImage(cid: Int, size: Int): SingleImageData? = suspendCoroutine {
        client.newCall(
            Request.Builder()
                .url("http://42.192.196.215:8080/classification/images?id=$cid&limit=$size")
                .build()
        ).enqueue(
            object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    it.resumeWithException(e)
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val body = response.body!!.string()
                    val data = gson.fromJson<BaseResponse<Pager<SingleImageData>>>(body, object : TypeToken<BaseResponse<Pager<SingleImageData>>>() {}.type)
                    it.resume(data.data.data.randomOrNull())
                }
            }
        )
    }

    private suspend fun getClassificationSize(cid: Int): Int = suspendCoroutine {
        client.newCall(
            Request.Builder()
                .url("http://42.192.196.215:8080/classification/images?id=${cid}")
                .build()
        ).enqueue(
            object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    it.resumeWithException(e)
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val body = response.body!!.string()
                    val data = gson.fromJson<BaseResponse<Pager<SingleImageData>>>(body, object : TypeToken<BaseResponse<Pager<SingleImageData>>>() {}.type)
                    it.resume(data.data.size)
                }
            }
        )
    }

    object Conf : AutoSavePluginData("persecution") {
        val flanPics: MutableList<String> by value {  }
    }
}