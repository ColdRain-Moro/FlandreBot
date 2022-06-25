package kim.bifrost.rain.flandre.data.persecution

import kotlinx.serialization.Serializable

/**
 * kim.bifrost.rain.flandre.data.persecution.ClassificationData
 * persecution
 *
 * @author 寒雨
 * @since 2022/3/6 0:38
 **/
@Serializable
data class ClassificationData(
    val id: Int,
    val name: String,
    val avatar: String,
    val description: String
)