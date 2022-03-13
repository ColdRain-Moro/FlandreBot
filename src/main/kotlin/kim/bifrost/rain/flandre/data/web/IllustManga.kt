package kim.bifrost.rain.flandre.data.web

data class IllustManga(
    val bookmarkRanges: List<BookmarkRange>,
    val `data`: List<Data>,
    val total: Int
)