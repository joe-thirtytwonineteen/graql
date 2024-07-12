package com.thirtytwonineteen.graql.lib.delegates.impl

import com.thirtytwonineteen.graql.lib.delegates.GraQLBatchLoaderFactory
import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import org.dataloader.BatchLoader
import java.lang.reflect.Method
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

