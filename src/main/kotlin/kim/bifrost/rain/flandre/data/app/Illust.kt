package kim.bifrost.rain.flandre.data.app

data class Illust(
    val caption: String,
    val create_date: String,
    val height: Int,
    val id: Int,
    val image_urls: ImageUrls,
    val is_bookmarked: Boolean,
    val is_muted: Boolean,
    val meta_pages: List<MetaPage>,
    val meta_single_page: MetaSinglePage,
    val page_count: Int,
    val restrict: Int,
    val sanity_level: Int,
    val series: Any,
    val tags: List<Tag>,
    val title: String,
    val tools: List<String>,
    val total_bookmarks: Int,
    val total_view: Int,
    val type: String,
    val user: User,
    val visible: Boolean,
    val width: Int,
    val x_restrict: Int
)