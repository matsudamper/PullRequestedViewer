package lib.undecorated

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import util.DisplayUtil
import java.awt.Rectangle
import java.beans.PropertyChangeListener

@Composable
fun displayChangedJFrameReLayout() {
    val window = LocalAppWindow.current
    val listener = remember {
        fun updateMaximizedBounds() {
            val bounds = DisplayUtil.getUsableBounds(window.window.graphicsConfiguration.device)
            window.window.maximizedBounds = Rectangle(0, 0, bounds.width, bounds.height)
        }
        updateMaximizedBounds()
        PropertyChangeListener {
            window.window.addNotify()

            updateMaximizedBounds()
        }
    }

    remember {
        object : RememberObserver {
            override fun onAbandoned() {}
            override fun onForgotten() {
                window.window.removePropertyChangeListener(listener)
            }

            override fun onRemembered() {
                window.window.addPropertyChangeListener(listener)
            }
        }
    }
}
