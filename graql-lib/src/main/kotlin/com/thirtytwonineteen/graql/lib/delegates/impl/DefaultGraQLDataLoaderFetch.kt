package com.thirtytwonineteen.graql.lib.delegates.impl

import com.thirtytwonineteen.graql.lib.delegates.GraQLDelegatingFetch
import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory

class DefaultGraQLDataLoaderFetch (
    override val type: String,
    override val field:String,
    val dataLoaderName: String,
    exceptionHandler: GraQLGlobalExceptionHandler,
): GraQLDelegatingFetch<Any>, AbstractGraQLDelegate(exceptionHandler) {

    override fun get(env: DataFetchingEnvironment): Any? {
        return withExceptionHandling(
            {
                val loader = env.getDataLoader<Any, Any>(dataLoaderName)
                    ?: throw RuntimeException("Could not find a DataLoader named ${dataLoaderName}")

                loader.load( env.getSource() )
            },
            {
                "Error delegating to dataLoader \"${dataLoaderName}\": your client should get an error message. Exception is logged."
            }
        )
    }
}


