package util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

@Composable
fun <R> rememberWithCoroutineScope(key: Any? = null, block: (CoroutineScope) -> R): R {
    val scope = rememberCoroutineScope()
    return remember(key) { block(scope) }
}