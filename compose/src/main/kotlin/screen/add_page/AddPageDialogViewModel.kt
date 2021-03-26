package screen.add_page

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import net.matsudamper.review_requested.repository.local.ISettingsRepository
import net.matsudamper.review_requested.repository.local.SettingData
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

class AddPageDialogViewModel(
    private val id: String?,
    override val coroutineContext: CoroutineContext,
    private val settingRepository: ISettingsRepository,
) : CoroutineScope {
    val nameText = mutableStateOf(TextFieldValue(""))
    val repositories = mutableStateOf<List<Repository>>(listOf())
    val pageTypeChecked = mutableStateOf(PageType.REQUESTED)

    init {
        run {
            id ?: return@run
            val settings = settingRepository.settingDataFlow.value ?: return@run
            val page = settings.pages.firstOrNull { it.id == id } ?: return@run

            nameText.value = TextFieldValue(page.name)
            repositories.value = page.repositories.map {
                Repository.fromString(it.organization, it.name)
            }
        }
    }

    fun save() {
        val id = id ?: System.currentTimeMillis().toString()
        settingRepository.update { data ->
            val pages = data.pages.toMutableList()

            val insertIndex = pages.indexOfFirst { it.id == id }.takeUnless { it < 0 }.let { index ->
                if (index == null) {
                    pages.size
                } else {
                    pages.removeAt(index)
                    index
                }
            }

            data.copy(
                pages = pages.apply {
                    add(
                        insertIndex,
                        SettingData.PageSettings(
                            id = id,
                            name = nameText.value.text,
                            repositories = repositories.value
                                .filterNot {
                                    listOf(it.organization, it.repositoryName)
                                        .map { it.value.text.isEmpty() }
                                        .all { it }
                                }.map {
                                SettingData.PageSettings.Repository(
                                    it.organization.value.text, it.repositoryName.value.text
                                )
                            }
                        )
                    )
                }
            )
        }
    }

    fun delete() {
        val id = id ?: return
        settingRepository.update { data ->
            val index = data.pages.indexOfFirst { it.id == id }.takeUnless { it < 0 } ?: return@update data
            data.copy(
                pages = data.pages.toMutableList().apply {
                    removeAt(index)
                }
            )
        }
    }

    enum class PageType {
        REQUEST,
        REQUESTED,
        ;
    }

    data class Repository(
        val organization: MutableState<TextFieldValue>,
        val repositoryName: MutableState<TextFieldValue>,
    ) {
        companion object {
            fun empty() = fromString(
                organization = "",
                repositoryName = "",
            )

            fun fromString(
                organization: String,
                repositoryName: String
            ): Repository {
                return Repository(
                    organization = mutableStateOf(TextFieldValue(organization)),
                    repositoryName = mutableStateOf(TextFieldValue(repositoryName)),
                )
            }
        }
    }
}