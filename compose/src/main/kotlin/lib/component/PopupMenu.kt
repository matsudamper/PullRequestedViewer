package lib.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.width
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Suppress("FunctionName")
@Composable
fun PopupMenu(menuVisibility: MutableState<Boolean>, scope: @Composable ColumnScope.() -> Unit) {
    if (menuVisibility.value) {
        Popup(
            alignment = Alignment.TopEnd,
            offset = IntOffset(-20, 20),
            isFocusable = true,
            onDismissRequest = { menuVisibility.value = false }
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .width(100.dp)
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides TextStyle(color = Color.Black),
                    LocalRippleTheme provides object : RippleTheme {
                        @Composable
                        override fun defaultColor(): Color {
                            return RippleTheme.defaultRippleColor(
                                Color.DarkGray,
                                MaterialTheme.colors.isLight
                            )
                        }

                        @Composable
                        override fun rippleAlpha(): RippleAlpha {
                            return RippleTheme.defaultRippleAlpha(
                                contentColor = LocalContentColor.current,
                                lightTheme = MaterialTheme.colors.isLight
                            )
                        }

                    },
                ) {
                    ProvideTextStyle(value = MaterialTheme.typography.body1, content = {
                        scope(this)
                    })
                }
            }
        }

    }
}