package net.matsudamper.review_requested.repository.local

import org.koin.core.module.Module
import org.koin.dsl.module

public object SettingRepositoryModule {
    public val module: Module = module {
        single<ISettingsRepository> { SettingsRepository() }
    }
}