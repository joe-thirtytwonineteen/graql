package com.thirtytwonineteen.graql.lib.delegates.impl

import com.thirtytwonineteen.graql.lib.delegates.GraQLDelegatingBatchLoader
import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutorService

class DefaultGraQLDelegatingBatchLoader(
    override val dataLoaderName:String,
    val method: Method,
    val target: Any,
    val executor: ExecutorService,
    exceptionHandler: GraQLGlobalExceptionHandler,
): GraQLDelegatingBatchLoader<Any, Any>, AbstractGraQLDelegate(exceptionHandler) {

    override fun load(keys: List<Any>): CompletionStage<List<Any>> =
        CompletableFuture.supplyAsync({
            val list = withExceptionHandling(
                {
                    val candidate = method.invoke(target, keys)

                    if (!List::class.java.isAssignableFrom(candidate.javaClass)) {
                        throw IllegalArgumentException("Cannot delegate loading to ${target::class.java.simpleName}::${method.name} because it did not return a List")
                    }

                    candidate

                },
                { "Error delegating to ${target::class.simpleName}::${method.name}: your client should get an error message. Exception is logged." }
            ) as List<Any>

            list
        }, executor)
}