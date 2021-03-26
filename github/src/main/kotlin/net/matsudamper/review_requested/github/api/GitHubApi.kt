package net.matsudamper.review_requested.github.api;


import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.github.PullRequestQuery
import com.github.type.CustomType
import net.matsudamper.review_requested.github.adapter.CustomTypeDataTimeAdapter
import net.matsudamper.review_requested.github.adapter.CustomTypeURIAdapter
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import java.util.*

class GitHubApi(
    TOKEN: String,
) {

    private val gitHubApolloClient = ApolloClient.builder()
        .addCustomTypeAdapter(CustomType.URI, CustomTypeURIAdapter)
        .addCustomTypeAdapter(CustomType.DATETIME, CustomTypeDataTimeAdapter)
        .okHttpClient(
            OkHttpClient.Builder()
                .authenticator(
                    Authenticator { _, response ->
                        return@Authenticator response.request.newBuilder()
                            .addHeader("Authorization", "Bearer $TOKEN")
                            .build()
                    }
                )
                .build()
        )
        .serverUrl("https://api.github.com/graphql")
        .build()

    suspend fun getOpenPullRequests(
        ownerName: String,
        repositoryName: String
    ): PullRequestQuery.Data {
        val response = gitHubApolloClient.query(PullRequestQuery(ownerName, repositoryName))
            .await()

        return response.data!!
    }
}

