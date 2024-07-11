package com.thirtytwonineteen.graql.lib.mapping.gson

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    override fun serialize(localDateTime: LocalDate, srcType: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(formatter.format(localDateTime))
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDate {
        return LocalDate.parse(json.asString, formatter)
    }

    companion object {
        private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }
}