package kim.bifrost.rain.flandre

import com.google.gson.Gson
import kim.bifrost.rain.flandre.data.Illust
import kim.bifrost.rain.flandre.data.SearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.closeQuietly
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * kim.bifrost.rain.flandre.PixivPics
 * Flandre
 *
 * @author 寒雨
 * @since 2022/1/8 13:20
 **/
object PixivPics : CoroutineScope by Flandre {

    private val client = OkHttpClient()
    private val gson = Gson()
    private const val TAG = "フランドール･スカーレット"
    private const val TAG_SUFFIX = "+1000users入り"

    fun init() {
        GlobalEventChannel.parentScope(this).subscribeAlways<GroupMessageEvent> {
            if (message.content == "来张芙兰") {
                launch {
                    if (Conf.apiKey.isEmpty()) {
                        group.sendMessage("未配置apiKey.")
                        return@launch
                    }
                    search().apply {
                        val item = get(Random.nextInt(size))
                        val url = if (item.page_count > 1) {
                            item.meta_pages[Random.nextInt(item.page_count)].image_urls.original
                        } else {
                            item.meta_single_page.original_image_url
                        }.replace("\\/", "/")
                            .replace("i.pximg.net", "i.acgmx.com")
                        val img = getImage(url)?.toExternalResource() ?: return@launch let {
                            group.sendMessage("内部错误: 不存在的图片")
                        }
                        val imageId = img.uploadAsImage(group).imageId
                        img.closeQuietly()
                        val receipt = group.sendMessage(Image(imageId))
                        if (item.sanity_level == 6) {
                            // 涩图将在30秒后撤回
                            delay(TimeUnit.SECONDS.toMillis(30))
                            receipt.recall()
                        }
                    }
                }
            }
        }
    }

    private suspend fun search(): List<Illust> = coroutineScope {
        val request = Request.Builder()
            .addHeader("token", Conf.apiKey)
            .url("https://api.acgmx.com/public/search?q=${URLEncoder.encode(TAG + TAG_SUFFIX, "utf-8")}&offset=${Conf.range}")
            .build()
        val json = client.newCall(request)
            .execute()
            .body!!
            .string()
        val obj = gson.fromJson(json, SearchResult::class.java)
        obj.illusts
    }

    private suspend fun getImage(url: String): ByteArray? = coroutineScope {
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).execute().body?.bytes()
    }

    object Conf : AutoSavePluginConfig("pixiv_pics") {
        val apiKey: String by value("")
        val range: Int by value(30)
    }
}