package net.matsudamper.review_requested.repository.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal class SettingsRepository : ISettingsRepository {
    private val json = Json {
        prettyPrint = true
    }
    private val file = File("settings.json")

    override val settingDataFlow: MutableStateFlow<SettingData?> = MutableStateFlow(null)

    override fun init() {
        settingDataFlow.value = if (file.exists().not()) {
            SettingData(
                token = "",
                pages = listOf()
            )
        } else {
            val text = file.readText()
            runCatching {
                json.decodeFromString<SettingData>(text)
            }.onFailure {
                throw JsonParseException()
            }.getOrThrow()
        }
    }

    override fun update(block: (SettingData) -> SettingData) {
        val newSetting = block(settingDataFlow.value!!)
        file.writeText(json.encodeToString(newSetting))
        settingDataFlow.value = newSetting
    }
}
