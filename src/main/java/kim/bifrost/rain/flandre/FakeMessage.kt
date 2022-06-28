package kim.bifrost.rain.flandre

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kim.bifrost.rain.flandre.lib.catchAll
import kim.bifrost.rain.flandre.lib.catching
import kim.bifrost.rain.flandre.lib.tryRun
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.message.data.toForwardMessage

/**
 * kim.bifrost.rain.flandre.FakeMessage
 * Flandre
 *
 * @author 寒雨
 * @since 2022/6/28 15:09
 */
class FakeMessageCommand : CompositeCommand(
    owner = Flandre,
    primaryName = "fakeMessage",
    description = "发送假消息"
) {

    private val gson = GsonBuilder().create()

    @SubCommand
    suspend fun UserCommandSender.single(user: User, message: Message) {
        sendMessage(message.toForwardMessage(user))
    }

    @SubCommand
    suspend fun UserCommandSender.single(id: Long, name: String, message: Message) {
        sendMessage(message.toForwardMessage(id, name))
    }

    /**
     * 解析json生成多段信息
     *
     * @param json
     */
    @SubCommand
    suspend fun UserCommandSender.generate(json: String) {
        tryRun {
            val data = gson.fromJson(json, JsonFakeMessageContainer::class.java)
            val message = data.messages.let {
                buildForwardMessage(subject) {
                    it.forEach { msg ->
                        add(msg.id, msg.name, msg.toMessage())
                    }
                }
            }
            sendMessage(message)
        } catchAll {
            sendMessage("解析json失败")
        }
    }
}

data class JsonFakeMessageContainer(
    val messages: List<JsonFakeMessage>
)

data class JsonFakeMessage(
    val id: Long,
    val name: String,
    val message: String
) {
    fun toMessage() : Message {
        return message.deserializeMiraiCode()
    }
}