package kim.bifrost.rain.flandre

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NudgeEvent
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import java.lang.reflect.Modifier
import kotlin.random.Random

/**
 * kim.bifrost.rain.flandre
 * AntiCowEmoji
 *
 * @author 寒雨
 * @since 2021/12/1 17:11
 **/

object Flandre : KotlinPlugin(
    JvmPluginDescription(
        id = "kim.bifrost.rain.flandre.Flandre",
        version = "1.0.0"
    ) {
        name("Flandre")
        author("Rain")
    }
) {

    override fun onEnable() {
        PixivPics.Conf.reload()
        Persecution.Conf.reload()
        // 代码执行模块
        CodeRunner.init(this)
        Persecution.init()
        PixivPics.init()
        // 发病模块
        Morbidity.init()
        // 戳一戳回应
        GlobalEventChannel.parentScope(this).subscribeAlways<NudgeEvent> {
            if (target.id == bot.id) {
                from.nudge().sendTo(this.subject)
            }
        }
    }
}

