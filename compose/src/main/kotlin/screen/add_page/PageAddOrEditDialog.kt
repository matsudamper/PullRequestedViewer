package screen.add_page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lib.AbstractDialogWrapper
import lib.component.DesktopTopAppBar
import lib.kradiance.FrameRadiance
import lib.undecorated.WindowDecorationInsteadState
import lib.undecorated.ignoreWindowDecorationInstead
import lib.undecorated.windowDecorationInstead
import org.koin.core.context.GlobalContext
import util.rememberWithCoroutineScope
import androidx.compose.ui.text.TextStyle

/**
 * @param id null is add new
 */
class PageAddOrEditDialog private constructor(
    title: String,
    private val id: String?
) : AbstractDialogWrapper(
    title = title,
    undecorated = true,
    size = IntSize(400, 500)
) {
    @Composable
    override fun body() {
        val windowDecorationInsteadState = remember { WindowDecorationInsteadState() }
        val viewModel = rememberWithCoroutineScope {
            GlobalContext.get().run {
                AddPageDialogViewModel(id, it.coroutineContext, get())
            }
        }

        FrameRadiance(
            minHeight = 1000
        ) {
            MaterialTheme {
                Column {
                    DesktopTopAppBar(
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
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        value = viewModel.nameText.value,
                        label = { Text("List Name") },
                        onValueChange = { viewModel.nameText.value = it }
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Row {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    text = "organization",
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    text = "repository",
                                )
                            }
                        }

                        LazyColumn {
                            items(
                                count = viewModel.repositories.value.size,
                                key = { index -> viewModel.repositories.value[index] }
                            ) { index ->
                                val item = viewModel.repositories.value[index]

                                Row {
                                    val textStyle = TextStyle.Default.copy(
                                        fontSize = 20.sp
                                    )
                                    BasicTextField(
                                        modifier = Modifier.weight(1f),
                                        value = item.organization.value,
                                        textStyle = textStyle,
                                        singleLine = true,
                                        onValueChange = { item.organization.value = it }
                                    )

                                    Text(
                                        modifier = Modifier.padding(horizontal = 4.dp),
                                        style = textStyle,
                                        text = "/",
                                    )

                                    BasicTextField(
                                        modifier = Modifier.weight(1f),
                                        value = item.repositoryName.value,
                                        textStyle = textStyle,
                                        onValueChange = { item.repositoryName.value = it }
                                    )
                                }
                            }
                        }

                        Row {
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        viewModel.repositories.value =
                                            viewModel.repositories.value + AddPageDialogViewModel.Repository.empty()
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Add"
                                )
                                Text("Add")
                            }
                        }
                    }


                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .weight(1f),
                    ) {
                        Row(
                            modifier = Modifier.align(Alignment.Bottom)
                                .padding(bottom = 8.dp),
                        ) {
                            if (id != null) {
                                OutlinedButton(
                                    modifier = Modifier
                                        .padding(end = 8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color.Red
                                    ),
                                    onClick = {
                                        viewModel.delete()
                                        dismiss()
                                    }
                                ) {
                                    Text("Delete")
                                }
                            }
                            Button(
                                modifier = Modifier
                                    .padding(end = 8.dp),
                                onClick = {
                                    viewModel.save()
                                    dismiss()
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

    companion object {
        fun typeAdd(): PageAddOrEditDialog {
            return PageAddOrEditDialog(id = null, title = "Add")
        }

        fun typeEdit(id: String): PageAddOrEditDialog {
            return PageAddOrEditDialog(id = id, title = "Edit")
        }
    }
}
