package lib.undecorated

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import com.sun.jna.platform.win32.*
import util.DisplayUtil
import java.awt.Rectangle
import java.awt.event.MouseEvent
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * val manager = remember { WindowDecoratedInsteadStateManager() }
 */
@Composable
fun Modifier.windowDecorationInstead(
        state: WindowDecorationInsteadState,
        overrideDoubleClickEvent: (() -> Unit)? = null,
): Modifier {
    val window = LocalAppWindow.current
    val beforeWindowState = remember { mutableStateOf(Rectangle()) }

    fun restoreWindow() {
        val rect = beforeWindowState.value
        window.window.setSize(rect.width, rect.height)
        window.window.setLocation(rect.x, rect.y)
    }

    val innerDoubleClickEvent = overrideDoubleClickEvent ?: {
        if (DisplayUtil.isFullScreen(window.window)) {
            restoreWindow()
        } else {
            val device = window.window.graphicsConfiguration.device
            val bounds = DisplayUtil.getUsableBounds(device)
            val position = DisplayUtil.getUsableDisplayZeroPoint(device)

            beforeWindowState.value = Rectangle().apply {
                x = window.window.x
                y = window.window.y
                width = window.window.width
                height = window.window.height
            }

            window.window.setSize(bounds.width, bounds.height)
            window.window.setLocation(
                    position.x,
                    position.y,
            )
        }
    }

    return composed {
        pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    val firstEvent = awaitPointerEvent()
                    if (state.enabled.value.not()) return@awaitPointerEventScope

                    val firstWindowPointer = firstEvent.mouseEvent?.point ?: return@awaitPointerEventScope
                    val firstEventParWidth = firstWindowPointer.x.toFloat() /
                            (firstEvent.mouseEvent?.component?.width ?: return@awaitPointerEventScope)
                    val firstGlobalPointer = firstEvent.mouseEvent?.locationOnScreen ?: return@awaitPointerEventScope

                    while (true) {
                        val currentEvent = awaitPointerEvent()
                        val currentGlobalPointer = currentEvent.mouseEvent?.locationOnScreen ?: break
                        val clickComponentX = (
                                (firstEvent.mouseEvent?.component?.width ?: return@awaitPointerEventScope)
                                        * firstEventParWidth
                                ).toInt()

                        if (DisplayUtil.isFullScreen(window.window).not()) {
                            window.setLocation(
                                    (currentGlobalPointer.x - clickComponentX),
                                    (currentGlobalPointer.y - firstWindowPointer.y),
                            )
                        } else {
                            val distance = (firstGlobalPointer.x - currentGlobalPointer.x).toDouble().pow(2)
                                    .plus((firstGlobalPointer.y - currentGlobalPointer.y).toDouble().pow(2))
                                    .let { sqrt(it) }

                            if (distance > 50) {
                                restoreWindow()
                            }
                        }

                        when (currentEvent.mouseEvent?.id) {
                            null,
                            MouseEvent.MOUSE_RELEASED -> {
                                if (firstEvent.mouseEvent?.clickCount == 2) {
                                    innerDoubleClickEvent()
                                }
                                break
                            }
                        }
                    }
                }
            }
        }
    }
}
