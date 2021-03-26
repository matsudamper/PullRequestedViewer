import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.MenuBar
import com.sun.jna.*
import kotlinx.coroutines.launch
import lib.component.DesktopTopAppBar
import lib.component.PopupMenu
import lib.component.drawer.DrawerState
import lib.kradiance.FrameRadiance
import lib.undecorated.WindowDecorationInsteadState
import lib.undecorated.displayChangedJFrameReLayout
import lib.undecorated.ignoreWindowDecorationInstead
import lib.undecorated.windowDecorationInstead
import screen.main.MainContents
import com.sun.jna.platform.WindowUtils
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinUser
import util.Hoge
import java.awt.Color
import java.awt.Menu
import javax.swing.*
import javax.swing.plaf.multi.MultiLookAndFeel
import javax.swing.plaf.ColorUIResource

import javax.swing.plaf.metal.DefaultMetalTheme
import javax.swing.plaf.metal.MetalLookAndFeel


interface Shcore : Library {
    fun GetDpiForMonitor(
            hmonitor: WinUser.HMONITOR,
            dpiType: WinDef.UINT,
            dpiX: WinDef.UINT,
            dpiY: WinDef.UINT
    ): WinNT.HRESULT
    companion object {
        val INSTANCE: Shcore = Native.load("shcore", Shcore::class.java)
    }
}

@Suppress("FunctionName")
fun MainWindow() = Window(
        title = "Review",
        undecorated = true,
        size = IntSize(400, 600),
) {
    val window = LocalAppWindow.current
    val coroutineScope = rememberCoroutineScope()

    displayChangedJFrameReLayout()
    FrameRadiance {
        MaterialTheme {
            val menuVisibility = remember { mutableStateOf(false) }
            val drawerState = remember { DrawerState() }

            Column {
                val windowDecorationInsteadState = remember { WindowDecorationInsteadState() }
                val menuProvider: MutableState<@Composable () -> Unit> = remember {
                    mutableStateOf({})
                }

                DesktopTopAppBar(
                        title = {
                            Text(window.title)
                        },
                        navigationIcon = {
                            IconButton(
                                    modifier = Modifier
                                            .ignoreWindowDecorationInstead(windowDecorationInsteadState),
                                    onClick = { coroutineScope.launch { drawerState.toggle() } },
                            ) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(
                                    modifier = Modifier
                                            .ignoreWindowDecorationInstead(windowDecorationInsteadState),
                                    onClick = {
                                        menuVisibility.value = true
                                    }
                            ) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "Info")
                            }

                            PopupMenu(menuVisibility = menuVisibility) {
                                menuProvider.value()
                            }
                        },
                        modifier = Modifier.windowDecorationInstead(windowDecorationInsteadState),
                )

                MainContents.show(
                        menuVisibility = menuVisibility,
                        menuProvider = menuProvider,
                        drawerState = drawerState,
                )
            }

            window.keyboard.setShortcut(Key.Escape) {
                if (menuVisibility.value) {
                    menuVisibility.value = false
                }
            }
        }
    }
}
