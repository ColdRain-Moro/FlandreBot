package kim.bifrost.rain.flandre

import kotlinx.coroutines.CoroutineScope
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.util.safeCast
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote

/**
 * kim.bifrost.rain.flandre.Persecution
 * Flandre
 * 迫害！
 *
 * @author 寒雨
 * @since 2022/3/4 13:21
 **/
object Persecution : CoroutineScope by Flandre {

    fun init() {
        GlobalEventChannel.parentScope(this).subscribeAlways<GroupMessageEvent> {
            // 迫害东方厨
            if (message.serializeToMiraiCode() == At(bot).serializeToMiraiCode()) {
//                println(1)
                group.sendMessage(Conf.flanPics.randomOrNull()?.deserializeMiraiCode() ?: return@subscribeAlways)
                return@subscribeAlways
            }
//            println(message.contentsList().map { it.javaClass.simpleName })
            if (message.contentsList().contains(At(bot))
                && message.anyIsInstance<PlainText>()
                && message.anyIsInstance<Image>()
                && message.any { it.safeCast<PlainText>()?.content?.contains("收入图库") == true }
            ) {
                val img = message[Image]!!
                Conf.flanPics.add(img.serializeToMiraiCode())
                group.sendMessage(message.quote() + "哼，已经记住啦！")
            }
        }
    }

    object Conf : AutoSavePluginData("persecution") {
        val flanPics: MutableList<String> by value {  }
    }
}