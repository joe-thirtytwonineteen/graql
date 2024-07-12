package com.thirtytwonineteen.graql.lib.delegates.impl

import com.thirtytwonineteen.graql.lib.delegates.GraQLMappedBatchLoaderFactory
import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import org.dataloader.MappedBatchLoader
import java.lang.reflect.Method
import java.util.concurrent.ExecutorService

class DefaultGraQLMappedBatchLoaderFactory(
    override val dataLoaderName:String,
    val method: Method,
    val target: Any,
    val executor: ExecutorService,
    val exceptionHandler: GraQLGlobalExceptionHandler,
): GraQLMappedBatchLoaderFactory<Any, Any> {

    override fun createLoader(): MappedBatchLoader<Any, Any> {
        return DefaultGraQLMappedBatchLoader(
            dataLoaderName,
            method,
            target,
            executor,
            exceptionHandler
        )
    }
}

