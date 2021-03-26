package net.matsudamper.review_requested.repository.local

import kotlinx.coroutines.flow.StateFlow

public interface ISettingsRepository {
    @Throws(JsonParseException::class)
    public fun init()
    public val  settingDataFlow: StateFlow<SettingData?>
    public fun update(block: (SettingData) -> SettingData)
}
