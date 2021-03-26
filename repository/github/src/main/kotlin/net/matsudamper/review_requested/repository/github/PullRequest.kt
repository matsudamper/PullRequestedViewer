package net.matsudamper.review_requested.repository.github

import java.net.URI
import java.util.*

public data class PullRequest(
    val viewerUserName : String,
    val repositoryOwner: String,
    val repositoryName: String,
    val pullRequestOwner: User,
    val reviewRequests: List<User>,
    val latestReviews: List<Review>,
    val mergeReady: Boolean,
    val reviewDecision: ReviewDecision,
    val title: String,
    val url: URI,
    val createdAt: Calendar,
    val updatedAt: Calendar,
) {
    public data class Review(
        val user: User,
        val status: Status
    ) {
        public enum class Status {
            APPROVE,
            NONE,
            REQUEST_CHANGES,
            COMMENTED,
            REJECT,
            ;
        }
    }

    public data class User(
        val name: String,
        val avaterUrl: URI,
    )

    public enum class ReviewDecision {
        APPROVED,
        CHANGES_REQUESTED,
        REVIEW_REQUIRED,
        UNKNOWN,
        ;
    }
}
