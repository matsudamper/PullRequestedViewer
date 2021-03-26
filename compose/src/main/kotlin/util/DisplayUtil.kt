package util

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.platform.WindowUtils
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinUser
import java.awt.*
import javax.swing.FocusManager
import javax.swing.JFrame
import kotlin.math.abs
import kotlin.math.max

@Suppress("FunctionName")
interface Hoge : Library {
    fun GetDpiForWindow(hwnd: WinDef.HWND): WinDef.UINT

    companion object {
        val INSTANCE: Hoge = Native.load("user32", Hoge::class.java)
    }
}

object DisplayUtil {

    /**
     * Windows awt bug
     */
    private fun getScreenInsets(gd: GraphicsDevice): Insets {
        val gc = gd.defaultConfiguration
        val javaInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc)

        val instance = runCatching { User32.INSTANCE }.getOrNull()
        if (instance != null) {
            val point = gd.defaultConfiguration.bounds.let {
                WinDef.POINT.ByValue(it.x, it.y)
            }

            val monitor = User32.INSTANCE.MonitorFromPoint(point, WinUser.MONITOR_DEFAULTTONEAREST)
            val info = WinUser.MONITORINFOEX().also {
                User32.INSTANCE.GetMonitorInfo(monitor, it)
            }

            return Insets(
                    abs(info.rcMonitor.top - info.rcWork.top),
                    abs(info.rcMonitor.left - info.rcWork.left),
                    abs(info.rcMonitor.bottom - info.rcWork.bottom),
                    abs(info.rcMonitor.right - info.rcWork.right),
            )
        }

        return javaInsets
    }

    fun getUsableBounds(gd: GraphicsDevice): Rectangle {
        val gc = gd.defaultConfiguration
        val insets = getScreenInsets(gd)
        val usableBounds = gc.bounds
        usableBounds.x += insets.left
        usableBounds.y += insets.top
        usableBounds.width -= insets.left + insets.right
        usableBounds.height -= insets.top + insets.bottom
        return usableBounds
    }

    fun getUsableDisplayZeroPoint(gd: GraphicsDevice): Point {
        val gc = gd.defaultConfiguration
        val insets = getScreenInsets(gd)
        val usableBounds = gc.bounds
        return Point(usableBounds.x + insets.left, usableBounds.y + insets.top)
    }

    fun isFullScreen(frame: JFrame): Boolean {
        val device = frame.graphicsConfiguration.device
        val bounds = getUsableBounds(device)
        val position = getUsableDisplayZeroPoint(device)

        return frame.size.width == bounds.width &&
                frame.size.height == bounds.height &&
                frame.location.x == position.x &&
                frame.location.y == position.y
    }
}