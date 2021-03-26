package screen.main

import PullRequestPage
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.dp
import lib.component.drawer.Drawer
import lib.component.drawer.DrawerState
import lib.rememberDialog
import model.GitHubOpenPullRequestCacheModel
import model.IOpenPullRequestCacheModel
import net.matsudamper.review_requested.repository.github.IGitHubRepository
import net.matsudamper.review_requested.repository.local.ISettingsRepository
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import screen.PullRequestPageViewModel
import screen.add_page.PageAddOrEditDialog
import screen.setting.SettingDialog
import util.rememberWithCoroutineScope


object MainContents {
    @Composable
    fun show(
        menuVisibility: MutableState<Boolean>,
        drawerState: DrawerState,
        menuProvider: MutableState<@Composable () -> Unit>
    ) {
        val selectedIdState = remember { mutableStateOf<String?>(null) }
        rememberWithCoroutineScope { scope ->
            GlobalContext.get().apply {
                loadKoinModules(
                    @Suppress("RemoveExplicitTypeArguments")
                    module(override = true) {
                        val settings = get<ISettingsRepository>()
                        single<IOpenPullRequestCacheModel> {
                            GitHubOpenPullRequestCacheModel(
                                gitHubRepository = get<IGitHubRepository> {
                                    IGitHubRepository.Companion.Parameters(
                                        token = settings.settingDataFlow.value?.token
                                    ).toParameters()
                                },
                            )
                        }
                    }
                )
            }
        }
        Drawer(
            drawerState = drawerState,
            drawerWidth = 200.dp,
            minContentWidth = 300.dp,
            contentKey = selectedIdState.value,
            drawerContent = {
                DrawerContent {
                    selectedIdState.value = it
                }
            }
        ) { selectedId ->
            selectedId ?: return@Drawer

            val viewModel = rememberWithCoroutineScope { scope ->
                val koin = GlobalContext.get()
                PullRequestPageViewModel(
                    id = selectedId,
                    openPullRequestCacheModel = koin.get(),
                    settingsRepository = koin.get(),
                    coroutineContext = scope.coroutineContext,
                ).also { it.initialFetch() }
            }

            val pages = remember {
                listOf(
                    TabDef(
                        title = "REQUESTED",
                        body = {
                            PullRequestPage().show(viewModel, PullRequestPage.Type.REQUESTED)
                        },
                    ),
                    TabDef(
                        title = "REQUEST",
                        body = {
                            PullRequestPage().show(viewModel, PullRequestPage.Type.REQUEST)
                        }
                    ),
                    TabDef(
                        title = "DONE",
                        body = {
                            PullRequestPage().show(viewModel, PullRequestPage.Type.DONE)
                        }
                    ),
                )
            }

            val tabIndex = remember { mutableStateOf(0) }

            menuProvider.value = {
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.fetch()
                        menuVisibility.value = false
                    },
                ) { Text(modifier = Modifier, text = "Refresh") }
            }

            Column {
                TabRow(tabIndex.value) {
                    pages.forEachIndexed { index, it ->
                        Tab(
                            selected = index == tabIndex.value,
                            text = @Composable {
                                Text(it.title)
                            },
                            onClick = { tabIndex.value = index }
                        )
                    }
                }
                pages[tabIndex.value].body()
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun DrawerContent(selected: (String) -> Unit) {
        val drawerViewModel = rememberWithCoroutineScope { scope ->
            GlobalContext.get().run {
                MainContentsViewModel(
                    settingRepository = get(),
                    coroutineContext = scope.coroutineContext,
                )
            }
        }
        val window = LocalAppWindow.current
        val settingDialog = rememberDialog { SettingDialog() }
        val buttonModifier = Modifier.height(48.dp).fillMaxWidth()
        val iconModifier = Modifier
            .padding(end = ButtonDefaults.IconSpacing)
            .size(ButtonDefaults.IconSize)

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) {
                val items = drawerViewModel.pages.collectAsState().value.map {
                    DrawerItem.Page(it)
                }.plus(
                    DrawerItem.Add
                )
                LazyColumn {
                    items(
                        count = items.size,
                        key = { index -> items[index] }
                    ) { index ->
                        val item = items[index]
                        when (item) {
                            is DrawerItem.Page -> {
                                val updatePageDialog = rememberDialog { PageAddOrEditDialog.typeEdit(item.value.id) }
                                val isHover = remember { mutableStateOf(false) }
                                TextButton(
                                    modifier = buttonModifier
                                        .pointerMoveFilter(
                                            onEnter = {
                                                isHover.value = true
                                                return@pointerMoveFilter false
                                            },
                                            onExit = {
                                                isHover.value = false
                                                return@pointerMoveFilter false
                                            },
                                        ),
                                    onClick = {
                                        selected(item.value.id)
                                    },
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Row(modifier = Modifier.fillMaxSize()) {
                                            Text(
                                                modifier = Modifier.align(Alignment.CenterVertically),
                                                text = item.value.name
                                            )
                                        }
                                        if (isHover.value) {
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                Row(modifier = Modifier.align(Alignment.End).fillMaxHeight()) {
                                                    Icon(
                                                        modifier = Modifier.align(Alignment.CenterVertically)
                                                            .clickable {
                                                                updatePageDialog.show()
                                                            },
                                                        imageVector = Icons.Filled.Settings,
                                                        contentDescription = "Settings"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            DrawerItem.Add -> {
                                val addPageDialog = rememberDialog { PageAddOrEditDialog.typeAdd() }
                                TextButton(modifier = buttonModifier, onClick = {
                                    addPageDialog.show()
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Add",
                                        modifier = iconModifier
                                    )
                                    Text("ADD")
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .shadow(4.dp)
                    .background(MaterialTheme.colors.background)
            ) {
                TextButton(modifier = buttonModifier, onClick = {
                    settingDialog.show()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        modifier = iconModifier
                    )
                    Text("Settings")
                }
                TextButton(modifier = buttonModifier, onClick = {
                    window.close()
                }) {
                    Text("Close")
                }
            }
        }
    }

    data class TabDef(
        val body: @Composable () -> Unit,
        val title: String,
    )

    sealed class DrawerItem {
        data class Page(val value: MainContentsViewModel.Page) : DrawerItem()
        object Add : DrawerItem()
    }
}