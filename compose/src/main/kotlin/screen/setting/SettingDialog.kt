package screen.setting

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import lib.AbstractDialogWrapper
import lib.kradiance.FrameRadiance
import org.koin.core.context.GlobalContext
import lib.undecorated.WindowDecorationInsteadState
import lib.undecorated.ignoreWindowDecorationInstead
import lib.undecorated.windowDecorationInstead
import util.rememberWithCoroutineScope
import java.awt.event.MouseEvent


class SettingDialog : AbstractDialogWrapper(
    title = "Settings",
    undecorated = true,
) {

    @Composable
    override fun body() {
        val viewModel = rememberWithCoroutineScope {
            SettingDialogViewModel(
                coroutineContext = it.coroutineContext,
                settingRepository = GlobalContext.get().get(),
            )
        }
        val windowDecorationInsteadState = remember { WindowDecorationInsteadState() }

        FrameRadiance {
            MaterialTheme {
                Column {
                    TopAppBar(
                        modifier = Modifier
                            .windowDecorationInstead(windowDecorationInsteadState),
                        title = {
                            Text(title)
                        },
                        actions = {
                            IconButton(
                                modifier = Modifier
                                    .ignoreWindowDecorationInstead(windowDecorationInsteadState),
                                onClick = {
                                    dismiss()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "CLOSE",
                                )
                            }
                        },
                        elevation = 2.dp,
                    )

                    OutlinedTextField(
                        value = viewModel.tokenText.value,
                        trailingIcon = {
                            TextButton(
                                modifier = Modifier.pointerInput(Unit) {
                                    forEachGesture {
                                        awaitPointerEventScope {
                                            awaitPointerEvent()
                                            val secondEvent = awaitPointerEvent().mouseEvent
                                                ?: return@awaitPointerEventScope
                                            when (secondEvent.id) {
                                                MouseEvent.MOUSE_RELEASED -> {
                                                    viewModel.pasteToken()
                                                    return@awaitPointerEventScope
                                                }
                                            }
                                        }
                                    }
                                },
                                onClick = {
                                    println("Click")
                                }
                            ) {
                                Text("PASTE")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        onValueChange = { viewModel.tokenText.value = it },
                        keyboardOptions = KeyboardOptions(),
                        label = { Text("TOKEN") },
                        singleLine = true,
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .weight(1f),
                    ) {
                        Button(
                            modifier = Modifier
                                .padding(end = 8.dp, bottom = 8.dp)
                                .align(Alignment.Bottom),
                            onClick = {
                                viewModel.save()
                            }
                        ) {
                            Text("SAVE")
                        }
                    }
                }
            }
        }
    }
}
