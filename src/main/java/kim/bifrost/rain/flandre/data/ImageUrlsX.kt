package kim.bifrost.rain.flandre.data

import kotlinx.serialization.Serializable

@Serializable
data class ImageUrlsX(
    val large: String,
    val medium: String,
    val original: String,
    val square_medium: String
)