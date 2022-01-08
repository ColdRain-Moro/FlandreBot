package kim.bifrost.rain.flandre.data

data class SearchResult(
    val illusts: List<Illust>,
    val next_url: String,
    val search_span_limit: Int
)