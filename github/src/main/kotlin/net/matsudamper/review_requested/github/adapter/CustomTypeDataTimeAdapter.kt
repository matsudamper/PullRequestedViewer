package net.matsudamper.review_requested.github.adapter

import com.apollographql.apollo.api.CustomTypeAdapter
import com.apollographql.apollo.api.CustomTypeValue
import java.net.URI
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal object CustomTypeDataTimeAdapter : CustomTypeAdapter<Calendar> {
    private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    override fun decode(value: CustomTypeValue<*>): Calendar {
        return Calendar.getInstance().also {
            it.timeInMillis = format
                .parse(value.value.toString()).time
        }
    }

    override fun encode(value: Calendar): CustomTypeValue<*> {
        return CustomTypeValue.fromRawValue(format.format(value.time))
    }
}
