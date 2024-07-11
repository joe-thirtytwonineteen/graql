package com.thirtytwonineteen.graql.lib.exceptions

import com.thirtytwonineteen.graql.lib.event.GraQLScanningComplete
import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import graphql.execution.SimpleDataFetcherExceptionHandler
import io.micronaut.context.event.ApplicationEventListener
import jakarta.inject.Singleton
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CompletableFuture


@Singleton
class GraQLGlobalExceptionHandler(): SimpleDataFetcherExceptionHandler(), ApplicationEventListener<GraQLScanningComplete> {

    override fun onApplicationEvent(event: GraQLScanningComplete) {
        exceptionTranslators = event.results.exceptionTranslators
    }

    lateinit var exceptionTranslators:Map<Class<Throwable>, GraQLExceptionTranslatorReference>

    fun actualExceptionFor( exception : Throwable ) : Throwable {
        return when {
            exception is InvocationTargetException -> ( exception ).targetException
            else -> exception
        }
    }

    fun exceptionTranslatorFor(actualException : Throwable ) : GraQLExceptionTranslatorReference? {
        return exceptionTranslators.get( actualException::class.java )
    }

    fun hasExceptionTranslatorFor( exception : Throwable ) : Boolean {
        return exceptionTranslatorFor( actualExceptionFor( exception ) ) != null
    }

    override fun handleException(handlerParameters: DataFetcherExceptionHandlerParameters): CompletableFuture<DataFetcherExceptionHandlerResult> {
        val ex = actualExceptionFor( handlerParameters.exception )
        val exceptionTranslator:GraQLExceptionTranslatorReference? = exceptionTranslatorFor( ex )

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