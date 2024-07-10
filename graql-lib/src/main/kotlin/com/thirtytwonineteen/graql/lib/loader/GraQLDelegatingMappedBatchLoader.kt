package com.thirtytwonineteen.graql.lib.loader

import org.dataloader.MappedBatchLoader
import java.lang.reflect.Method
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutorService

class GraQLDelegatingMappedBatchLoader(
    val dataLoaderName:String,
    val method: Method,
    val target: Any,
    val executor:ExecutorService
): MappedBatchLoader<Any, Any> {

    override fun load(keys: MutableSet<Any>): CompletionStage<Map<Any, Any>> =
        CompletableFuture.supplyAsync( {
            val res:Any = method.invoke( target, keys )

            if ( res !is Map<*,*>) {
                throw IllegalArgumentException("Cannot delegate loading to ${target::class.java.simpleName}::${method.name} because it did not return Map<*, *>")
            }

            res as Map<Any, Any>
        }, executor)
}