package lib.undecorated

import kotlinx.coroutines.flow.MutableStateFlow

data class WindowDecorationInsteadState(
    val enabled: MutableStateFlow<Boolean> = MutableStateFlow(true)
)