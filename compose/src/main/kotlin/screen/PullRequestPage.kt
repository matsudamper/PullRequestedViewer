import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import screen.PullRequestPageViewModel
import util.ImageLoader
import java.awt.Desktop

@Suppress("FunctionName")
class PullRequestPage {
    @Composable
    fun show(
        viewModel: PullRequestPageViewModel,
        type: Type
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                val items = remember { viewModel.getListItems(type) }
                val count = items.collectAsState().value.size
                LazyColumn {
                    items(
                        count = count,
                        key = { index -> items.value[index] },
                    ) { index ->
                        val item = items.value[index]

                        Column {
                            Row(
                                modifier = Modifier
                                    .height(100.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        val desktop = Desktop.getDesktop()
                                        desktop.browse(item.url)
                                    }
                            ) {
                                Image(
                                    modifier = Modifier
                                        .width(48.dp)
                                        .height(48.dp),
                                    contentDescription = "user icon",
                                    bitmap = ImageLoader.imageFromUrl(item.pullRequestOwner.avaterUrl),
                                )
                                Text(item.title)
                            }

                            contentColorFor(Color.Black)
                        }

                        Divider(color = Color(0x44666666))
                    }
                }

                SnackbarHost(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(4.dp),
                    hostState = viewModel.snackbarState
                ) {
                    Snackbar { Text(it.message) }
                }
            }
        }
    }

    enum class Type {
        REQUESTED, REQUEST, DONE
    }
}
