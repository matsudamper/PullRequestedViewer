package lib

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun <T : AbstractDialogWrapper> rememberDialog(block: () -> T): T {
    val dialog = remember { block() }
    dialog.put()
    return dialog
}

abstract class AbstractDialogWrapper(
    val title: String,
    val undecorated: Boolean = false,
    val size: IntSize = IntSize(400, 250)
) {
    private val state: MutableStateFlow<Boolean> = MutableStateFlow(false)

    // It will be crash when trying to close while focused TextField.
    private val preState = mutableStateOf(false)

    @Composable
    protected abstract fun body()

    @Composable
    fun put() {
        if (state.collectAsState().value) {
            Dialog(
                onDismissRequest = {},
                properties = DialogProperties(
                    title = title,
                    undecorated = undecorated,
                    size = size,
                ),
            ) {
                if (preState.value) {
                    body()
                } else {
                    state.value = false
                }
            }
        }
    }

    fun show() {
        preState.value = true
        state.value = true
    }

    fun dismiss() {
        preState.value = false
    }
}