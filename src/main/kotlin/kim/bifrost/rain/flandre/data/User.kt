package kim.bifrost.rain.flandre.data

data class User(
    val account: String,
    val id: Int,
    val is_followed: Boolean,
    val name: String,
    val profile_image_urls: ProfileImageUrls
)