package model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import net.matsudamper.review_requested.repository.github.IGitHubRepository
import net.matsudamper.review_requested.repository.github.PullRequest

class GitHubOpenPullRequestCacheModel(
    private val gitHubRepository: IGitHubRepository,
) : IOpenPullRequestCacheModel {
    override val pullRequests: MutableStateFlow<Map<IOpenPullRequestCacheModel.Repository, List<PullRequest>>> =
        MutableStateFlow(mutableMapOf())

    override suspend fun fetch(
        repositories: List<IOpenPullRequestCacheModel.Repository>,
        resultNotify: (IOpenPullRequestCacheModel.Results) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val result = repositories
                .map { repository ->
                    runCatching {
                        repository to gitHubRepository.getOpenPullRequests(
                            repository.ownerName,
                            repository.repositoryName
                        )
                    }.onFailure {
                        it.printStackTrace()
                        resultNotify(IOpenPullRequestCacheModel.Results.Error(repository))
                    }.getOrNull()
                }
                .filterNotNull()
                .toMap()

            pullRequests.value = pullRequests.value.toMutableMap().apply {
                putAll(result)
            }
            resultNotify(IOpenPullRequestCacheModel.Results.Success)
        }
    }
}
