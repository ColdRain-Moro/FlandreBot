package kim.bifrost.rain.flandre

import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import kotlinx.coroutines.*
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import javax.script.ScriptEngine
import kotlin.concurrent.thread

/**
 * kim.bifrost.rain.flandre.CodeRunner
 * Flandre
 * 代码执行模块
 * 单次代码执行时长不能超过五秒
 * 否则强制中断
 *
 * @author 寒雨
 * @since 2021/12/22 22:51
 **/

object CodeRunner : CoroutineScope by Flandre {

    private var currentScriptEngine: ScriptEngine? = null

    private var currentUser: User? = null

    @Suppress("DEPRECATION")
    private val nashornScriptEngineFactory = NashornScriptEngineFactory()

    fun init(plugin: KotlinPlugin) {
        GlobalEventChannel.parentScope(plugin).subscribeAlways<GroupMessageEvent> {
            if (sender.permission >= MemberPermission.ADMINISTRATOR) {
                val msg = message.contentToString()
                if (msg == "/shell") {
                    if (currentScriptEngine == null) {
                        currentScriptEngine = nashornScriptEngineFactory.getScriptEngine("--language=es6")
                        currentScriptEngine?.put("event", this)
                        currentUser = sender
                        group.sendMessage(message.quote() + "已进入JavaScript Shell")
                    } else {
                        currentScriptEngine = null
                        currentUser = null
                        group.sendMessage(message.quote() + "已退出JavaScript Shell")
                    }
                    return@subscribeAlways
                }
                if (sender == currentUser) {
                    launch(Dispatchers.IO) {
                        kotlin.runCatching {
                            val res = currentScriptEngine?.eval(message.contentToString())
                            group.sendMessage("返回值: $res")
                        }.onFailure {
                            group.sendMessage("脚本执行异常: " + it.javaClass.name + ": ${it.message}")
                        }
                    }
                }
            }
        }
    }
}

class ErrorObj(val msg: String?)