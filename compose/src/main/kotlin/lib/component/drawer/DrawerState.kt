package lib.component.drawer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class DrawerState(
    val state: MutableState<State> = mutableStateOf(State.CLOSE)
) {

    fun open() {
        state.value = State.OPEN
    }

    fun close() {
        state.value = State.CLOSE
    }

    fun toggle() {
        state.value = when (state.value) {
            State.OPEN -> State.CLOSE
            State.CLOSE -> State.OPEN
        }
    }

    enum class State {
        OPEN,
        CLOSE,
        ;
    }
}
