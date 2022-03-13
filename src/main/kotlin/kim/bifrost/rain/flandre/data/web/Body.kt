package kim.bifrost.rain.flandre.data.web

data class Body(
    val extraData: ExtraData,
    val illustManga: IllustManga,
    val popular: Popular,
    val relatedTags: List<String>,
    val tagTranslation: Map<String, TagTranslation>,
    val zoneConfig: ZoneConfig
)