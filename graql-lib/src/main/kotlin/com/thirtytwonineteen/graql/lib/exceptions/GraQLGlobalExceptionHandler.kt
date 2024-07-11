package com.thirtytwonineteen.graql.lib.exceptions

import com.thirtytwonineteen.graql.GraQL
import com.thirtytwonineteen.graql.GraQLComponent
import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import graphql.execution.SimpleDataFetcherExceptionHandler
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CompletableFuture


@GraQLComponent
class GraQLGlobalExceptionHandler(
    val graQL: GraQL
): SimpleDataFetcherExceptionHandler() {
    override fun handleException(handlerParameters: DataFetcherExceptionHandlerParameters): CompletableFuture<DataFetcherExceptionHandlerResult> {
        val ex = when {
            handlerParameters.exception is InvocationTargetException -> ( handlerParameters.exception as InvocationTargetException).targetException
            else -> handlerParameters.exception
        }

        val exceptionTranslator:GraQLExceptionTranslatorReference? = graQL.getExceptionTranslatorFor( ex )

        if ( exceptionTranslator != null ) {
            val builder = GraphQLError.newError()
                .location(handlerParameters.sourceLocation)
                .path(handlerParameters.path)

            exceptionTranslator.validateAndInvoke( ex, builder as GraphqlErrorBuilder<*>)

            val graphqlError = builder.build()

            val result = DataFetcherExceptionHandlerResult.newResult()
                .error(graphqlError)
                .build()

            return CompletableFuture.completedFuture(result)
        } else {
            return super.handleException(handlerParameters)
        }
    }
}