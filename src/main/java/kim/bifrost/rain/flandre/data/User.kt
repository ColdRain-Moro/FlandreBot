package kim.bifrost.rain.flandre.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val account: String,
    val id: Int,
    val is_followed: Boolean,
    val name: String,
    val profile_image_urls: ProfileImageUrls
)