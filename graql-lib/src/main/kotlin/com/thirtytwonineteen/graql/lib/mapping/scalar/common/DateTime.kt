package com.thirtytwonineteen.graql.lib.mapping.scalar.common

import com.thirtytwonineteen.graql.GraQLScalar
import graphql.GraphQLContext
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingSerializeException
import jakarta.inject.Singleton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@GraQLScalar
@Singleton
class DateTime: Coercing<LocalDateTime, String> {

    override fun serialize(input: Any): String {
        if (input is LocalDateTime) {
            return input.format(DateTimeFormatter.ISO_DATE_TIME)
        } else {
            throw CoercingSerializeException("Not a valid DateTime")
        }
    }

    override fun parseValue(input: Any): LocalDateTime {
        return LocalDateTime.parse(input.toString(), DateTimeFormatter.ISO_DATE_TIME)
    }

    fun parseLiteral(
        input: Value<*>,
    ): LocalDateTime? {
        if (input is StringValue) {
            return LocalDateTime.parse((input as StringValue).getValue(), DateTimeFormatter.ISO_DATE_TIME)
        }

        throw CoercingParseLiteralException("Value is not a valid ISO date time")

    }

    override fun valueToLiteral(input: Any, graphQLContext: GraphQLContext, locale: Locale): Value<*> {
        return StringValue(this.serialize(input))
    }


}