package net.matsudamper.review_requested.github.adapter

import com.apollographql.apollo.api.ScalarType

public enum class CustomType : ScalarType {
    URI {
        override fun className(): String {
            return CustomTypeURIAdapter::class.java.name
        }

        override fun typeName(): String {
            return "URI"
        }
    },
}