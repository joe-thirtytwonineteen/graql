package com.thirtytwonineteen.graql.lib.mapping.gson

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    override fun serialize(localDateTime: LocalDateTime, srcType: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(formatter.format(localDateTime))
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime {
        return LocalDateTime.parse(json.asString, formatter)
    }

    companion object {
        private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss::SSS")
    }
}