package kim.bifrost.rain.flandre.data.persecution

import kotlinx.serialization.Serializable

/**
 * kim.bifrost.rain.flandre.data.persecution.Pager
 * persecution
 * 分页
 *
 * @author 寒雨
 * @since 2022/3/5 23:46
 **/
@Serializable
data class Pager<T>(
    val size: Int,
    val offset: Int,
    val data: List<T>
)