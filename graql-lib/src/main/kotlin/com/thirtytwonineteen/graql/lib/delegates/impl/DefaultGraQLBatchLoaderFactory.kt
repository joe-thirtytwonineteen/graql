package com.thirtytwonineteen.graql.lib.delegates.impl

import com.thirtytwonineteen.graql.lib.delegates.GraQLBatchLoaderFactory
import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import org.dataloader.BatchLoader
import java.lang.reflect.Method
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutorService

class DefaultGraQLBatchLoaderFactory(
    override val dataLoaderName:String,
    val method: Method,
    val target: Any,
    val executor: ExecutorService,
    val exceptionHandler: GraQLGlobalExceptionHandler,
): GraQLBatchLoaderFactory<Any, Any> {
    override fun createLoader():BatchLoader<Any, Any> {
        return DefaultGraQLBatchLoader(
            dataLoaderName,
            method,
            target,
            executor,
            exceptionHandler
        )
    }
}

class DefaultGraQLBatchLoader(
    val dataLoaderName:String,
    val method: Method,
    val target: Any,
    val executor: ExecutorService,
    exceptionHandler: GraQLGlobalExceptionHandler,
): BatchLoader<Any, Any>, AbstractGraQLDelegate(exceptionHandler) {

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

