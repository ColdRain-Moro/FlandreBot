import kim.bifrost.rain.flandre.PixivPics
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import javax.script.ScriptEngineManager
import kotlin.concurrent.thread

val scriptEngineManager = ScriptEngineManager()

/**
 * .Main
 * Flandre
 *
 * @author 寒雨
 * @since 2021/12/8 22:49
 **/
fun main() {
    val req = Request.Builder()
        .url("https://api.acgmx.com/public/search?q=${URLEncoder.encode("フランドール･スカーレット+1000users入り", "utf-8")}&offset=30")
        .build()
    val client = OkHttpClient()
    println(client.newCall(req)
        .execute()
        .body?.string())
}