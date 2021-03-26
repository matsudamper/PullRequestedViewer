package screen.main

import kotlinx.coroutines.CoroutineScope
import net.matsudamper.review_requested.repository.local.ISettingsRepository
import util.mapStateFlow
import kotlin.coroutines.CoroutineContext

class MainContentsViewModel(
    private val settingRepository: ISettingsRepository,
    override val coroutineContext: CoroutineContext,
) : CoroutineScope {
    val pages = settingRepository.settingDataFlow
        .mapStateFlow(this) {
            it ?: return@mapStateFlow emptyList()
            it.pages.map {
                Page(
                    id = it.id,
                    name = it.name
                )
            }
        }

    data class Page(
        val id: String,
        val name: String
    )
}