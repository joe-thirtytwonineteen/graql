package com.thirtytwonineteen.graql.lib.exceptions

import com.thirtytwonineteen.graql.GraQLComponent
import com.thirtytwonineteen.graql.GraQLExceptionHandler
import graphql.ErrorType
import graphql.GraphqlErrorBuilder
import io.micronaut.http.HttpStatus
import jakarta.validation.ConstraintViolationException

@GraQLComponent
class GraQLValidationExceptionHandler {

    @GraQLExceptionHandler
    fun handleValidationException(ex: ConstraintViolationException, builder: GraphqlErrorBuilder<*>) {
        val ext = ex.constraintViolations
            .fold( mutableMapOf<String, GraQLTypedError>()) { acc, it ->
                acc.put(
                    "${it.propertyPath}:${it.message}",
                    GraQLTypedError(
                        HttpStatus.BAD_REQUEST.name,
                        it.message,
                        it.propertyPath.toString(),
                    )
                )
                acc
            }

        builder
            .message("Validation error: see extensions for details")
            .extensions(ext as Map<String, Any>)
            .errorType( ErrorType.ValidationError )
    }

}