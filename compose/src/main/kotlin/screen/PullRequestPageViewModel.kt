package screen

import androidx.compose.material.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import model.IOpenPullRequestCacheModel
import net.matsudamper.review_requested.repository.github.PullRequest
import net.matsudamper.review_requested.repository.local.ISettingsRepository
import util.combineState
import util.mapStateFlow
import kotlin.coroutines.CoroutineContext


class PullRequestPageViewModel(
    private val id: String,
    private val openPullRequestCacheModel: IOpenPullRequestCacheModel,
    settingsRepository: ISettingsRepository,
    override val coroutineContext: CoroutineContext,
) : CoroutineScope {
    private val repositories = settingsRepository.settingDataFlow.mapStateFlow(this) { data ->
        val pages = data?.pages ?: listOf()

        pages.firstOrNull { it.id == id }?.repositories.orEmpty().map { repository ->
            IOpenPullRequestCacheModel.Repository(ownerName = repository.organization, repositoryName = repository.name)
        }
    }
    private val listItems: StateFlow<List<PullRequest>> = combineState(
        repositories, openPullRequestCacheModel.pullRequests
    ) { repositories, pullRequests ->
        repositories
            .mapNotNull { pullRequests[it] }
            .flatten()
    }

    // TODO error Snackbar
    val snackbarState = SnackbarHostState()

    fun initialFetch() {
        if (listItems.value.isEmpty()) {
            fetch()
        }
    }

    fun fetch() {
        launch {
            openPullRequestCacheModel.fetch(repositories = repositories.value) {
                when (it) {
                    is IOpenPullRequestCacheModel.Results.Error -> {
                        launch { snackbarState.showSnackbar("${it.repository}の取得でエラーが発生しました") }
                    }
                    IOpenPullRequestCacheModel.Results.Success -> {
                        launch { snackbarState.showSnackbar("更新しました") }
                    }
                }
            }
        }
    }

    fun getListItems(type: PullRequestPage.Type): StateFlow<List<PullRequest>> {
        return listItems.mapStateFlow(this) { listItems ->
            val sortedList = listItems
                .sortedByDescending { it.updatedAt }

            val requestPullRequest = sortedList.asSequence()
                .filter { it.pullRequestOwner.name == it.viewerUserName }

            fun Sequence<PullRequest.Review>.approved(): Boolean {
                return mapNotNull {
                    when (it.status) {
                        PullRequest.Review.Status.APPROVE -> true
                        PullRequest.Review.Status.REJECT,
                        PullRequest.Review.Status.REQUEST_CHANGES -> false
                        PullRequest.Review.Status.COMMENTED,
                        PullRequest.Review.Status.NONE -> null
                    }
                }.lastOrNull() ?: false
            }

            fun PullRequest.mineApproved(): Boolean {
                val myReviews = latestReviews.asSequence()
                    .filter { it.user.name == viewerUserName }

                return myReviews.approved()
            }

            when (type) {
                PullRequestPage.Type.REQUESTED -> {
                    sortedList.asSequence()
                        .filter { it.reviewRequests.map { it.name }.contains(it.viewerUserName) }
                        .filter { pullRequest -> pullRequest.mineApproved().not() }
                        .toList()
                }
                PullRequestPage.Type.REQUEST -> {
                    requestPullRequest
                        .filterNot { pullRequest ->
                            pullRequest.reviewDecision == PullRequest.ReviewDecision.APPROVED
                        }
                        .toList()
                }
                PullRequestPage.Type.DONE -> {
                    listOf(
                        sortedList.filter { pullRequest ->
                            pullRequest.latestReviews.any {
                                it.user.name == pullRequest.viewerUserName && it.status == PullRequest.Review.Status.APPROVE
                            }
                        },
                        requestPullRequest
                            .filter { pullRequest ->
                                pullRequest.reviewDecision == PullRequest.ReviewDecision.APPROVED
                            }
                            .toList(),
                    ).flatten()
                }
            }
        }
    }
}
