package kim.bifrost.rain.flandre.data

import kotlinx.serialization.Serializable

@Serializable
data class ImageUrls(
    val large: String,
    val medium: String,
    val square_medium: String
)