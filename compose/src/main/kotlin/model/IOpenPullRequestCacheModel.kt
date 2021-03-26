package model

import kotlinx.coroutines.flow.StateFlow
import net.matsudamper.review_requested.repository.github.PullRequest

interface IOpenPullRequestCacheModel {
    val pullRequests: StateFlow<Map<Repository, List<PullRequest>>>

    suspend fun fetch(repositories: List<Repository>, resultNotify: (Results) -> Unit)

    data class Repository(
        val ownerName: String,
        val repositoryName: String,
    ) {
        override fun toString(): String {
            return "$ownerName/$repositoryName"
        }
    }

    sealed class Results {
        object Success : Results()
        data class Error(val repository: Repository) : Results()
    }
}