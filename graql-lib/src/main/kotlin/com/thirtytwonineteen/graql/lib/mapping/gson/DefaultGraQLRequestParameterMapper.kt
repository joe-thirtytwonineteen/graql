package com.thirtytwonineteen.graql.lib.mapping.gson

import com.google.gson.GsonBuilder
import com.thirtytwonineteen.graql.lib.config.GraQLFactory
import com.thirtytwonineteen.graql.lib.mapping.GraQLRequestParameterMapper
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime

@Singleton
@Named("graQLRequestParameterMapper")
class DefaultGraQLRequestParameterMapper : GraQLRequestParameterMapper {
    companion object {
        private val LOG = LoggerFactory.getLogger(GraQLFactory::class.java)
    }
    override fun map( input: Any, desiredReturnType:Class<*> ):Any {
        val mapper = GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .create()

        var result: Any? = null
        try {
            val json = mapper.toJson(input)
            result = mapper.fromJson(json, desiredReturnType)
        } catch (e: Exception) {
            LOG.warn("Error converting request object for GraQL. Not halting: your client should get an error message", e)
        }

        return result!!
    }
}