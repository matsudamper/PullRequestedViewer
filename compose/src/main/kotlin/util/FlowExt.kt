package util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

fun <T, R> StateFlow<T>.mapStateFlow(coroutineScope: CoroutineScope, transformer: (T) -> R): StateFlow<R> {
    return this.map { value -> transformer(value) }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = transformer(value),
        )
}

fun <T1, T2, R> CoroutineScope.combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    transform: (a: T1, b: T2) -> R
): StateFlow<R> = combine(flow1, flow2) { a: T1, b: T2 ->
    transform(a, b)
}.stateIn(this, SharingStarted.Lazily, transform(flow1.value, flow2.value))

