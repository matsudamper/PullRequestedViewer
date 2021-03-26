package lib.kradiance

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import util.DisplayUtil
import java.awt.Cursor
import java.awt.event.MouseEvent
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener

// TODO: (minWidth, minHeight)
@Composable
@Suppress("FunctionName")
fun FrameRadiance(
    radianceSize: Dp = 1.dp,
    touchSize: Dp = 5.dp,
    minWidth : Int = 200,
    minHeight: Int = 300,
    block: @Composable () -> Unit
) {
    val window = LocalAppWindow.current
    val isActive = remember { mutableStateOf(false) }
    fun isResizable() = DisplayUtil.isFullScreen(window.window).not()
    fun update() {
        isActive.value = window.window.isFocused
    }

    val focusListener = remember {
        object : WindowFocusListener {
            init {
                update()
            }

            override fun windowGainedFocus(e: WindowEvent?) {
                update()
            }

            override fun windowLostFocus(e: WindowEvent?) {
                update()
            }
        }
    }
    remember {
        object : RememberObserver {
            override fun onAbandoned() {}

            override fun onRemembered() {
                window.window.addWindowFocusListener(focusListener)
            }

            override fun onForgotten() {
                window.window.removeWindowFocusListener(focusListener)
            }
        }
    }

    val activeColor = Color.Cyan
    val inActiveColor = Color.Black
    val currentColor = if (isActive.value) activeColor else inActiveColor

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(currentColor)
                .padding(radianceSize)
                .background(Color.White)
                .pointerMoveFilter(
                    onEnter = {
                        window.window.cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
                        return@pointerMoveFilter false
                    },
                )
        ) {
            block()
        }

        // Left
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(touchSize)
                .padding(vertical = touchSize)
                .pointerMoveFilter(
                    onEnter = {
                        if (isResizable()) {
                            window.window.cursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)
                        }
                        return@pointerMoveFilter false
                    },
                )
                .pointerInput(Unit) {
                    forEachGesture {
                        awaitPointerEventScope {
                            val firstEvent = awaitPointerEvent().mouseEvent ?: return@awaitPointerEventScope
                            val firstWindowWidth = window.width
                            while (true) {
                                if (isResizable().not()) break
                                val currentEvent = awaitPointerEvent().mouseEvent ?: break

                                val diff = currentEvent.locationOnScreen.x - firstEvent.locationOnScreen.x
                                val resizeWidth = firstWindowWidth - diff
                                if (minWidth <= resizeWidth) {
                                    window.setLocation(currentEvent.locationOnScreen.x, window.y)
                                    window.setSize(firstWindowWidth - diff, window.height)
                                }

                                when (currentEvent.id) {
                                    MouseEvent.MOUSE_RELEASED -> {
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
        ) {  }

        // Top
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(touchSize)
                .padding(horizontal = touchSize)
                .pointerMoveFilter(
                    onEnter = {
                        if (isResizable()) {
                            window.window.cursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)
                        }
                        return@pointerMoveFilter false
                    },
                )
                .pointerInput(Unit) {
                    forEachGesture {
                        awaitPointerEventScope {
                            val firstEvent = awaitPointerEvent().mouseEvent ?: return@awaitPointerEventScope
                            val firstWindowHeight = window.height
                            while (true) {
                                if (isResizable().not()) break
                                val currentEvent = awaitPointerEvent().mouseEvent ?: break

                                val diff = currentEvent.locationOnScreen.y - firstEvent.locationOnScreen.y
                                val resizeHeight = firstWindowHeight - diff
                                if (minHeight <= resizeHeight) {
                                    window.setLocation(window.x, currentEvent.locationOnScreen.y)
                                    window.setSize(window.width, firstWindowHeight - diff)
                                }

                                when (currentEvent.id) {
                                    MouseEvent.MOUSE_RELEASED -> {
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
        ) {  }

        // Right
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.TopEnd)
                .width(touchSize)
                .padding(vertical = touchSize)
                .pointerMoveFilter(
                    onMove = {
                        return@pointerMoveFilter false
                    },
                    onEnter = {
                        if (isResizable()) {
                            window.window.cursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)
                        }
                        return@pointerMoveFilter false
                    },
                )
                .pointerInput(Unit) {
                    forEachGesture {
                        awaitPointerEventScope {
                            val firstEvent = awaitPointerEvent().mouseEvent ?: return@awaitPointerEventScope
                            val firstWindowWidth = window.width
                            while (true) {
                                if (isResizable().not()) break
                                val currentEvent = awaitPointerEvent().mouseEvent ?: break

                                val diff = currentEvent.locationOnScreen.x - firstEvent.locationOnScreen.x
                                val resizeWidth = firstWindowWidth + diff
                                if (minWidth <= resizeWidth) {
                                    window.setSize(resizeWidth, window.height)
                                }

                                when (currentEvent.id) {
                                    MouseEvent.MOUSE_RELEASED -> {
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
        ) {  }

        // Bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .height(touchSize)
                .padding(horizontal = touchSize)
                .pointerMoveFilter(
                    onEnter = {
                        if (isResizable()) {
                            window.window.cursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR)
                        }
                        return@pointerMoveFilter false
                    },
                )
                .pointerInput(Unit) {
                    forEachGesture {
                        awaitPointerEventScope {
                            val firstEvent = awaitPointerEvent().mouseEvent ?: return@awaitPointerEventScope
                            val firstWindowHeight = window.height
                            while (true) {
                                if (isResizable().not()) break
                                val currentEvent = awaitPointerEvent().mouseEvent ?: break

                                val diff = currentEvent.locationOnScreen.y - firstEvent.locationOnScreen.y
                                val resizeHeight = firstWindowHeight + diff
                                if (minHeight <= resizeHeight) {
                                    window.setSize(window.width, resizeHeight)
                                }

                                when (currentEvent.id) {
                                    MouseEvent.MOUSE_RELEASED -> {
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
        ) {  }

        // Left-Top
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .height(touchSize)
                .width(touchSize)
                .pointerMoveFilter(
                    onEnter = {
                        if (isResizable()) {
                            window.window.cursor = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR)
                        }
                        return@pointerMoveFilter false
                    },
                )
                .pointerInput(Unit) {
                    forEachGesture {
                        awaitPointerEventScope {
                            val firstEvent = awaitPointerEvent().mouseEvent ?: return@awaitPointerEventScope
                            val firstWindowHeight = window.height
                            val firstWindowWidth = window.width
                            while (true) {
                                if (isResizable().not()) break
                                val currentEvent = awaitPointerEvent().mouseEvent ?: break

                                val diffX = currentEvent.locationOnScreen.x - firstEvent.locationOnScreen.x
                                val diffY = currentEvent.locationOnScreen.y - firstEvent.locationOnScreen.y
                                window.setLocation(currentEvent.locationOnScreen.x, currentEvent.locationOnScreen.y)
                                window.setSize(firstWindowWidth - diffX, firstWindowHeight - diffY)

                                when (currentEvent.id) {
                                    MouseEvent.MOUSE_RELEASED -> {
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
        ) {  }

        // Top-Right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .height(touchSize)
                .width(touchSize)
                .pointerMoveFilter(
                    onEnter = {
                        if (isResizable()) {
                            window.window.cursor = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR)
                        }
                        return@pointerMoveFilter false
                    },
                )
                .pointerInput(Unit) {
                    forEachGesture {
                        awaitPointerEventScope {
                            val firstEvent = awaitPointerEvent().mouseEvent ?: return@awaitPointerEventScope
                            val firstWindowHeight = window.height
                            val firstWindowWidth = window.width
                            while (true) {
                                if (isResizable().not()) break
                                val currentEvent = awaitPointerEvent().mouseEvent ?: break

                                val diffX = currentEvent.locationOnScreen.x - firstEvent.locationOnScreen.x
                                val diffY = currentEvent.locationOnScreen.y - firstEvent.locationOnScreen.y
                                window.setLocation(window.x, currentEvent.locationOnScreen.y)
                                window.setSize(firstWindowWidth + diffX, firstWindowHeight - diffY)

                                when (currentEvent.id) {
                                    MouseEvent.MOUSE_RELEASED -> {
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
        ) {  }

        // Right-bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .height(touchSize)
                .width(touchSize)
                .pointerMoveFilter(
                    onEnter = {
                        if (isResizable()) {
                            window.window.cursor = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR)
                        }
                        return@pointerMoveFilter false
                    },
                )
                .pointerInput(Unit) {
                    forEachGesture {
                        awaitPointerEventScope {
                            val firstEvent = awaitPointerEvent().mouseEvent ?: return@awaitPointerEventScope
                            val firstWindowHeight = window.height
                            val firstWindowWidth = window.width
                            while (true) {
                                if (isResizable().not()) break
                                val currentEvent = awaitPointerEvent().mouseEvent ?: break

                                val diffX = currentEvent.locationOnScreen.x - firstEvent.locationOnScreen.x
                                val diffY = currentEvent.locationOnScreen.y - firstEvent.locationOnScreen.y
                                window.setSize(firstWindowWidth + diffX, firstWindowHeight + diffY)

                                when (currentEvent.id) {
                                    MouseEvent.MOUSE_RELEASED -> {
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
        ) {  }

        // Bottom-Left
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .height(touchSize)
                .width(touchSize)
                .pointerMoveFilter(
                    onEnter = {
                        if (isResizable()) {
                            window.window.cursor = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR)
                        }
                        return@pointerMoveFilter false
                    },
                )
                .pointerInput(Unit) {
                    forEachGesture {
                        awaitPointerEventScope {
                            val firstEvent = awaitPointerEvent().mouseEvent ?: return@awaitPointerEventScope
                            val firstWindowHeight = window.height
                            val firstWindowWidth = window.width
                            while (true) {
                                if (isResizable().not()) break
                                val currentEvent = awaitPointerEvent().mouseEvent ?: break

                                val diffX = currentEvent.locationOnScreen.x - firstEvent.locationOnScreen.x
                                val diffY = currentEvent.locationOnScreen.y - firstEvent.locationOnScreen.y
                                window.setLocation(currentEvent.locationOnScreen.x, window.y)
                                window.setSize(firstWindowWidth - diffX, firstWindowHeight + diffY)

                                when (currentEvent.id) {
                                    MouseEvent.MOUSE_RELEASED -> {
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
        ) {  }
    }
}
