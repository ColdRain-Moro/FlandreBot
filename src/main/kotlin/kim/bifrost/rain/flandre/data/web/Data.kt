package kim.bifrost.rain.flandre.data.web

data class Data(
    val alt: String,
    val bookmarkData: Any,
    val createDate: String,
    val description: String,
    val height: Int,
    val id: String,
    val illustType: Int,
    val isBookmarkable: Boolean,
    val isMasked: Boolean,
    val isUnlisted: Boolean,
    val pageCount: Int,
    val profileImageUrl: String,
    val restrict: Int,
    val sl: Int,
    val tags: List<String>,
    val title: String,
    val titleCaptionTranslation: TitleCaptionTranslation,
    val updateDate: String,
    val url: String,
    val userId: String,
    val userName: String,
    val width: Int,
    val xRestrict: Int
)