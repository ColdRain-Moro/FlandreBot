package kim.bifrost.rain.flandre

import kotlinx.coroutines.*
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import javax.script.ScriptEngineManager
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
object CodeRunner {

    private var executing = false

    private val scriptEngineManager = ScriptEngineManager()

    fun init(plugin: KotlinPlugin) {
        GlobalEventChannel.parentScope(plugin).subscribeAlways<GroupMessageEvent> {
            if (sender.permission >= MemberPermission.ADMINISTRATOR) {
                val msg = message.contentToString()
                if (msg.startsWith("runJs") && !executing) {
                    kotlin.runCatching {
                        executing = true
                        val future = CompletableFuture<Any?>()
                        val thread = thread {
                            kotlin.runCatching {
                                val code = msg.removePrefix("runJs").replace("\\n", "")
                                val engine = scriptEngineManager.getEngineByName("js")
                                // 放入事件对象，从而使机器人的行为可以被脚本所控制
                                engine.put("event", this)
                                engine.eval(code)
                            }.onSuccess {
                                future.complete(it)
                            }.onFailure {
                                future.complete(ErrorObj(it.message))
                            }
                        }
                        val res = kotlin.runCatching { future.get(5, TimeUnit.SECONDS) }
                            .onFailure { thread.stop() }
                        res.getOrThrow()
                    }.onFailure {
                        subject.sendMessage("脚本执行错误: ${it.message}")
                    }.onSuccess {
                        it?.let {
                            if (it !is ErrorObj) subject.sendMessage("返回值: $it") else subject.sendMessage("脚本执行错误: ${it.msg}")
                        }
                    }
                    executing = false
                }
            }
        }
    }
}

class ErrorObj(val msg: String?)