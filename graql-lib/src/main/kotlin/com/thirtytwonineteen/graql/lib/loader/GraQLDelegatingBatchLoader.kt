package com.thirtytwonineteen.graql.lib.loader

import com.thirtytwonineteen.graql.GraQLDelegate
import org.dataloader.BatchLoader
import org.dataloader.MappedBatchLoader
import java.lang.reflect.Method
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutorService

interface GraQLDelegatingBatchLoader<K, V>:BatchLoader<K, V>, GraQLDelegate {
    val dataLoaderName:String
}

class DefaultGraQLDelegatingBatchLoader(
    override val dataLoaderName:String,
    val method: Method,
    val target: Any,
    val executor:ExecutorService
): GraQLDelegatingBatchLoader<Any, Any> {
    override fun load(keys: List<Any>): CompletionStage<List<Any>> =
        CompletableFuture.supplyAsync( {
            val res:Any = method.invoke( target, keys )

            if ( !List::class.java.isAssignableFrom(res.javaClass) ) {
                throw IllegalArgumentException("Cannot delegate loading to ${target::class.java.simpleName}::${method.name} because it did not return a List")
            }

            res as List<Any>
        }, executor)
}