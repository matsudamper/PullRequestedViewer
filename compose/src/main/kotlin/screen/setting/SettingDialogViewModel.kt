package screen.setting

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.matsudamper.review_requested.repository.local.ISettingsRepository
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import kotlin.coroutines.CoroutineContext

class SettingDialogViewModel(
    override val coroutineContext: CoroutineContext,
    private val settingRepository: ISettingsRepository,
) : CoroutineScope {
    val tokenText = mutableStateOf(TextFieldValue(settingRepository.settingDataFlow.value!!.token))

    fun save() {
        launch {
            settingRepository.update {
                it.copy(
                    token = tokenText.value.text
                )
            }
        }
    }

    fun pasteToken() {
        val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard

        val token = clipboard.getContents(null)
            .getTransferData(DataFlavor.stringFlavor)
                as? String ?: return

        tokenText.value = TextFieldValue(token)
    }
}