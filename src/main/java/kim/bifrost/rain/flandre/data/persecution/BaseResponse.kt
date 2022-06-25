package kim.bifrost.rain.flandre.data.persecution

import kotlinx.serialization.Serializable

/**
 * kim.bifrost.rain.flandre.data.persecution.BaseResponse
 * persecution
 *
 * @author 寒雨
 * @since 2022/3/5 14:38
 **/
@Serializable
data class BaseResponse<T>(
    // 正常时为0
    val errorCode: Int,
    val message: String,
    val data: T
)