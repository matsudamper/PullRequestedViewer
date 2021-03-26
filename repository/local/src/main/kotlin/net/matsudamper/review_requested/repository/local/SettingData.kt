package net.matsudamper.review_requested.repository.local;

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import net.matsudamper.review_requested.repository.local.lib.StringEnum

@Serializable
public data class SettingData(
    @SerialName("token") val token: String,
    @SerialName("pages") val pages: List<PageSettings>,
) {
    @Serializable
    public data class PageSettings(
        @SerialName("id") val id: String,
        @SerialName("pages") val name: String,
        @SerialName("repositories") val repositories: List<Repository>,
//        @SerialName("page_type") val pageType: PageType?
    ) {
        @Serializable(PageType.Serializer::class)
        public enum class PageType : StringEnum {
            REQUEST {
                override val label: String = "REQUEST"
            },

            REQUESTED {
                override val label: String = "REQUESTED"
            },
            ;

            internal object Serializer : StringEnum.Companion.Serializer<PageType>(PageType::class)
        }

        @Serializable
        public data class Repository(
            @SerialName("organization") val organization: String,
            @SerialName("name") val name: String,
        )
    }
}
