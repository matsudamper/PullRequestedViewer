package net.matsudamper.review_requested.github.api

import com.github.PullRequestQuery

interface IGitHubApi {
    suspend fun getOpenPullRequests(
        ownerName: String,
        repositoryName: String,
    ): PullRequestQuery.Data
}