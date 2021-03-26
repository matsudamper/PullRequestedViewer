package net.matsudamper.review_requested.repository.github

import org.koin.core.parameter.DefinitionParameters
import org.koin.core.parameter.parametersOf

public interface IGitHubRepository {
    public suspend fun getOpenPullRequests(owner: String, repository: String): List<PullRequest>


    public companion object {
        public class Parameters(
            internal val token: String?
        ) {
            public fun toParameters(): DefinitionParameters = parametersOf(this)
        }
    }
}