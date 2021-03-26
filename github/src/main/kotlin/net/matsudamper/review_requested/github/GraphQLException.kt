package net.matsudamper.review_requested.github

import com.apollographql.apollo.api.Error

class GraphQLException(val errors: List<Error>) : Exception(errors.map { it.message }.toString())