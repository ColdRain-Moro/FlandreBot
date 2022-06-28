package kim.bifrost.rain.flandre

import kim.bifrost.rain.flandre.lib.*
import kim.bifrost.rain.flandre.lib.gif.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.io.IOException
import java.io.InputStream


/**
 * kim.bifrost.rain.flandre.GifMake
 * Flandre
 *
 * @author 寒雨
 * @since 2022/6/23 14:46
 */
class GifMakeCommand : CompositeCommand(
    owner = Flandre,
    primaryName = "makeGif",
    description = "制图命令",
) {

    private val petService = BasePetService()
    private val plugin: JvmPlugin = Flandre
    private val repository = SingletonDocumentRepository<BaseServiceConfig>(
        plugin,
        plugin.resolveConfigFile("repository.json"),
        BaseServiceConfig::class.java
    ) { BaseServiceConfig() }

    init {
        repository.findSingleton().apply {
            petService.readBaseServiceConfig(this)
        }
        petService.readData(Flandre.dataFolder)
    }

    @SubCommand("摸摸", "petpet")
    suspend fun UserCommandSender.petpet(target: User) {
        useTemplateGeneral(this, "petpet", target)
    }

    @SubCommand("选择模板", "use")
    suspend fun UserCommandSender.use(petKey: String, target: User, vararg args: String) {
        useTemplateGeneral(this, petKey, target, *args)
    }

    private suspend fun useTemplateGeneral(sender: CommandSender, petkey: String, target: User, vararg petReplaceArgs: String) {
        // 准备制图参数
        val fromAvatarImage = if (sender.user != null) ImageSynthesis.getAvatarImage(sender.user!!.avatarUrl) else null
        val toAvatarImage = ImageSynthesis.getAvatarImage(target.avatarUrl)
        val textExtraData = TextExtraData("", "", "", petReplaceArgs.toList())
        // 制图
        val resultPair: Pair<InputStream, String> =
            petService.generateImage(petkey, AvatarExtraData(
                fromAvatar = fromAvatarImage,
                toAvatar = toAvatarImage,
            ), textExtraData, listOf())
        // 使用制图结果
        tryRun {
            resultPair.first.toExternalResource().use { externalResource ->
                val image = sender.subject!!.uploadImage(externalResource)
                sender.sendMessage(image)
            }
        } catching { e: Exception ->
                plugin.logger.error("使用petService resultPair时异常", e)
                sender.sendMessage("使用制图结果时异常")
        } finally {
            try {
                withContext(Dispatchers.IO) {
                    resultPair.first.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}