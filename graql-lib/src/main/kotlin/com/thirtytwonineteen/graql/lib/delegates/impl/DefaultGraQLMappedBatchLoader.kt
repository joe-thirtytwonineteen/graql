package com.thirtytwonineteen.graql.lib.delegates.impl

import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import org.dataloader.MappedBatchLoader
import java.lang.reflect.Method
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutorService

class DefaultGraQLMappedBatchLoader(
    val dataLoaderName:String,
    val method: Method,
    val target: Any,
    val executor: ExecutorService,
    exceptionHandler: GraQLGlobalExceptionHandler,
): MappedBatchLoader<Any, Any>, AbstractGraQLDelegate(exceptionHandler) {

    override fun load(keys: MutableSet<Any>): CompletionStage<Map<Any, Any>> =
        CompletableFuture.supplyAsync({

            val res = withExceptionHandling(
                {
                    val candidate = method.invoke(target, keys)

                    if (!Map::class.java.isAssignableFrom(candidate.javaClass)) {
                        throw IllegalArgumentException("Cannot delegate loading to ${target::class.java.simpleName}::${method.name} because it did not return Map<*, *>")
                    }

                    candidate
                },
                { "Error delegating to ${target::class.simpleName}::${method.name}: your client should get an error message. Exception is logged." }
            ) as Map<Any, Any>

            res
        }, executor)
}