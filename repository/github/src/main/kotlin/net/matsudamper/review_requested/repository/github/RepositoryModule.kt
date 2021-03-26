package net.matsudamper.review_requested.repository.github

import org.koin.core.module.Module
import org.koin.dsl.module

public object RepositoryModule {
    public val module: Module = module {
        factory<IGitHubRepository> { (arguments: IGitHubRepository.Companion.Parameters) ->
            GitHubApiRepository(arguments.token)
        }
    }
}