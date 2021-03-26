package lib.undecorated

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerMoveFilter


@Composable
fun Modifier.ignoreWindowDecorationInstead(
    state: WindowDecorationInsteadState,
) = composed {
    pointerMoveFilter(
        onEnter = {
            state.enabled.value = false
            return@pointerMoveFilter false
        },
        onExit = {
            state.enabled.value = true
            return@pointerMoveFilter false
        },
    )
}
