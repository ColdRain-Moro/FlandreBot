package kim.bifrost.rain.flandre

import com.google.gson.Gson
import kim.bifrost.rain.flandre.data.Illust
import kim.bifrost.rain.flandre.data.SearchResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import okhttp3.*
import okhttp3.internal.closeQuietly
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

/**
 * kim.bifrost.rain.flandre.PixivPics
 * Flandre
 *
 * @author 寒雨
 * @since 2022/1/8 13:20
 **/
object PixivPics : CoroutineScope by Flandre {

    private val remapper = mutableMapOf(
        "芙兰" to ("フランドール･スカーレット" to 1000),
        "蕾米" to ("レミリア・スカーレット" to 1000),
        "恋恋" to ("古明地こいし" to 1000),
        "东方" to ("東方" to 10000),
        "咲夜" to ("十六夜咲夜" to 1000),
        "早苗" to ("東風谷早苗" to 1000),
        "灵梦" to ("博麗霊夢" to 5000),
        "魔理沙" to ("霧雨魔理沙" to 1000),
        "爱丽丝" to ("アリス・マーガトロイド" to 1000),
        "咏唱组" to ("マリアリ" to 1000),
        "主角组" to ("レイマリ" to 1000),
        "黑丝" to ("黒スト" to 10000),
        "白丝" to ("白タイツ" to 10000),
        "随便什么丝" to ("ストッキング" to 10000),
        "涩图" to ("R-18" to 50000),
        "风景" to ("風景" to 10000),
        "女孩" to ("女の子" to 10000)
    )

    private val currentCounts = AtomicInteger()

    private val errorCounts = AtomicInteger()

    private var debug = false

    fun init() {
        launch {
            // 每分钟重置
            while (true) {
                delay(TimeUnit.MINUTES.toMillis(1))
                currentCounts.set(0)
                errorCounts.set(0)
            }
        }
        GlobalEventChannel.parentScope(this).subscribeAlways<GroupMessageEvent> {
            val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                launch {
                    errorCounts.getAndIncrement()
                    group.sendMessage(message.quote().plus("发生错误: ${throwable.localizedMessage}"))
                }
            }
            if (sender.id == 1335014319L && message.content == "/debug") {
                debug = !debug
                group.sendMessage("DebugMode: $debug")
                return@subscribeAlways
            }
            if (message.content == "/listmapper") {
                group.sendMessage("可用词条映射: ${remapper.keys.joinToString(", ")}")
                return@subscribeAlways
            }
            // 每分钟搜图数或错误数达到上限，直接返回
            if (currentCounts.get() >= 5 && errorCounts.get() >= 5) {
                return@subscribeAlways
            }
            if (message.content.startsWith("来张")) {
                val key = message.content
                    .removePrefix("来张")
                if (key.trim().isEmpty()) {
                    return@subscribeAlways
                }
                val limit = remapper[key]?.second ?: 1000
                val query = key.run {
                    var str = this
                    remapper.forEach { (k, v) ->
                        str = str.replace(k, v.first)
                    }
                    str
                } + "+${limit}users入り"
                launch(exceptionHandler) {
                    search(query).apply {
                        if (isEmpty()) {
                            group.sendMessage(message.quote().plus("没有找到符合条件的图片"))
                            return@apply
                        }
                        var tryCounts = 0
                        var item = get(Random.nextInt(size))
                        if (!Conf.allow_sex) {
                            while (item.sanity_level == 6) {
                                if (tryCounts >= 3) {
                                    group.sendMessage("不准涩涩！")
                                    return@launch
                                }
                                item = get(Random.nextInt(size))
                                tryCounts++
                            }
                        }
                        val url = if (item.page_count > 1) {
                            item.meta_pages[Random.nextInt(item.page_count)].image_urls.large
                        } else {
                            item.meta_single_page.original_image_url
                        }.replace("\\/", "/")
                            .replace("i.pximg.net", "pixiv-image-lv.pwp.link")
                            .replace("_webp", "")
                        if (debug) {
                            println(url)
                        }
                        val type = url.split(".").run { get(size - 1) }
                        val img = getImage(url)?.toExternalResource(type) ?: return@launch let {
                            group.sendMessage("内部错误: 不存在的图片")
                        }
                        val imageId = img.uploadAsImage(group).imageId
                        img.closeQuietly()
                        val receipt = group.sendMessage(Image(imageId))
                        currentCounts.getAndIncrement()
                        if (item.sanity_level == 6) {
                            // 涩图将在15秒后撤回
                            delay(TimeUnit.SECONDS.toMillis(15))
                            receipt.recall()
                        }
                    }
                }
            }
        }
    }

    private suspend fun search(key: String): List<Illust> = suspendCoroutine {
        val url = "https://api.acgmx.com/public/search?q=${
            URLEncoder.encode(
                key,
                "utf-8"
            )
        }&offset=${Random.nextInt(100)}"
        if (debug) {
            println(url)
        }
        val request = Request.Builder()
            .addHeader("token", Conf.apiKey)
            .url(url)
            .build()
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val json = response.body!!.string()
                    if (debug) {
                        println(json)
                    }
                    val obj = gson.fromJson(json, SearchResult::class.java)
                    it.resume(obj.illusts)
                }

            })
    }

    object Conf : AutoSavePluginConfig("pixiv_pics") {
        val apiKey: String by value("")
        val allow_sex: Boolean by value(false)
    }
}