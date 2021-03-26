import net.matsudamper.review_requested.repository.github.RepositoryModule
import net.matsudamper.review_requested.repository.local.ISettingsRepository
import net.matsudamper.review_requested.repository.local.SettingRepositoryModule
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import javax.swing.JFrame


class Main {
    companion object {
        private val settingRepository by lazy { GlobalContext.get().get<ISettingsRepository>() }

        @JvmStatic
        fun main(vararg arg: String) {
            startKoin {
                loadKoinModules(modules)
            }
            settingRepository.init()

            MainWindow()
        }

        val modules: List<Module> = listOf(
            RepositoryModule.module,
            SettingRepositoryModule.module,
        )
    }
}
