package lib.component.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
@Suppress("FunctionName")
fun <T> Drawer(
    drawerState: DrawerState,
    drawerWidth: Dp,
    minContentWidth: Dp,
    drawerContent: @Composable () -> Unit,
    contentKey: T,
    content: @Composable (T) -> Unit,
) {
    SubcomposeLayout(Modifier.fillMaxSize()) { constraint ->
        @Suppress("NAME_SHADOWING")
        val constraint = constraint.copy(minHeight = 0, minWidth = 0)

        val isExpandSize = constraint.maxWidth - (drawerWidth.value * density) >= minContentWidth.value

        val subCompose = object {
            val background = subcompose(ContentType.BackgroundDark) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.5f)
                        .background(Color.Black)
                        .clickable {
                            drawerState.close()
                        }
                ) { }
            }

            val main = subcompose(ContentType.Main(contentKey)) {
                content(contentKey)
            }

            val drawer = subcompose(ContentType.Drawer) {
                Box(
                    Modifier
                        .width(drawerWidth)
                        .fillMaxHeight()
                        .shadow(4.dp)
                        .background(MaterialTheme.colors.background)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { }
                ) { drawerContent() }
            }
        }

        when (drawerState.state.value) {
            DrawerState.State.OPEN -> {
                val drawerPlaceable = subCompose.drawer.map { it.measure(constraint) }

                if (isExpandSize) {
                    val measuredDrawerWidth = drawerPlaceable.fold(0) { left, right ->
                        left + right.width
                    }

                    val mainPlaceable = subCompose.main.map {
                        it.measure(constraint.copy(maxWidth = constraint.maxWidth - measuredDrawerWidth))
                    }

                    layout(constraint.maxWidth, constraint.maxHeight) {
                        drawerPlaceable.onEach { it.placeRelative(0, 0) }
                        mainPlaceable.onEach { it.placeRelative(measuredDrawerWidth, 0) }
                    }
                } else {
                    val mainPlaceable = subCompose.main.map { it.measure(constraint) }
                    val backgroundPlaceable = subCompose.background.map { it.measure(constraint) }

                    layout(constraint.maxWidth, constraint.maxHeight) {
                        mainPlaceable.onEach { it.placeRelative(0, 0) }
                        backgroundPlaceable.onEach { it.placeRelative(0, 0) }
                        drawerPlaceable.onEach { it.placeRelative(0, 0) }
                    }
                }
            }
            DrawerState.State.CLOSE -> {
                val mainPlaceable = subCompose.main.map { it.measure(constraint) }

                layout(constraint.maxWidth, constraint.maxHeight) {
                    mainPlaceable.onEach { it.placeRelative(0, 0) }
                }
            }
        }
    }
}

private sealed class ContentType {
    data class Main<T>(val key: T) : ContentType()
    object Drawer : ContentType()
    object BackgroundDark : ContentType()
}