package net.matsudamper.review_requested.repository.github

import com.github.type.MergeableState
import com.github.type.PullRequestReviewDecision
import com.github.type.PullRequestReviewState
import net.matsudamper.review_requested.github.api.GitHubApi
import java.lang.IllegalStateException


internal class GitHubApiRepository(
    private val token: String?
) : IGitHubRepository {
    override suspend fun getOpenPullRequests(owner: String, repository: String): List<PullRequest> {
        val token = token ?: throw IllegalStateException("tokenがnullです")
        val result = GitHubApi(token).getOpenPullRequests(owner, repository)

        return result.organization!!.repository!!.pullRequests.nodes.orEmpty().filterNotNull().map {
            PullRequest(
                viewerUserName = result.viewer.login,
                title = it.title,
                url = it.url,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                repositoryOwner = owner,
                repositoryName = repository,
                reviewRequests = it.reviewRequests?.nodes.orEmpty()
                    .mapNotNull { it?.requestedReviewer?.asUser }
                    .map {
                        PullRequest.User(
                            name = it.login,
                            avaterUrl = it.avatarUrl,
                        )
                    },
                pullRequestOwner = PullRequest.User(
                    name = it.author!!.login,
                    avaterUrl = it.author!!.avatarUrl,
                ),
                mergeReady = when(it.mergeable) {
                    MergeableState.CONFLICTING -> false
                    MergeableState.MERGEABLE -> true
                    MergeableState.UNKNOWN -> false
                    MergeableState.UNKNOWN__ -> false
                },
                reviewDecision = when(it.reviewDecision) {
                    PullRequestReviewDecision.APPROVED -> PullRequest.ReviewDecision.APPROVED
                    PullRequestReviewDecision.CHANGES_REQUESTED -> PullRequest.ReviewDecision.CHANGES_REQUESTED
                    PullRequestReviewDecision.REVIEW_REQUIRED -> PullRequest.ReviewDecision.REVIEW_REQUIRED
                    PullRequestReviewDecision.UNKNOWN__ -> PullRequest.ReviewDecision.UNKNOWN
                    null -> PullRequest.ReviewDecision.UNKNOWN
                },
                latestReviews = it.latestReviews?.nodes.orEmpty().filterNotNull().map { review ->
                    PullRequest.Review(
                        user = PullRequest.User(
                            name = review.author!!.login,
                            avaterUrl = review.author!!.avatarUrl,
                        ),
                        status = when (review.state) {
                            PullRequestReviewState.APPROVED -> PullRequest.Review.Status.APPROVE
                            PullRequestReviewState.CHANGES_REQUESTED -> PullRequest.Review.Status.REQUEST_CHANGES
                            PullRequestReviewState.DISMISSED -> PullRequest.Review.Status.REJECT
                            PullRequestReviewState.COMMENTED -> PullRequest.Review.Status.COMMENTED
                            PullRequestReviewState.PENDING -> PullRequest.Review.Status.NONE
                            PullRequestReviewState.UNKNOWN__ -> PullRequest.Review.Status.NONE
                        },
                    )
                },
            )
        }
    }
}