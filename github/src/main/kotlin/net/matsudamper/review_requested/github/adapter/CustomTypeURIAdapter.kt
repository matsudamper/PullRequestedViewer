package net.matsudamper.review_requested.github.adapter

import com.apollographql.apollo.api.CustomTypeAdapter
import com.apollographql.apollo.api.CustomTypeValue
import java.net.URI

internal object CustomTypeURIAdapter: CustomTypeAdapter<URI> {
    override fun decode(value: CustomTypeValue<*>): URI {
        return URI(value.value.toString())
    }

    override fun encode(value: URI): CustomTypeValue<*> {
        return CustomTypeValue.fromRawValue(value.toASCIIString())
    }
}
