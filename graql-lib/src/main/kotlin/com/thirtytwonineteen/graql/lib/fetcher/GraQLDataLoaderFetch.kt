package com.thirtytwonineteen.graql.lib.fetcher

import graphql.schema.DataFetchingEnvironment

class DefaultGraQLDataLoaderFetch (
    override val type: String,
    override val field:String,
    val dataLoaderName: String,
): GraQLDelegatingFetch<Any> {
    override fun get(env: DataFetchingEnvironment): Any? {
        val loader = env.getDataLoader<Any, Any>(dataLoaderName)
            ?: throw RuntimeException("Could not find a DataLoader named ${dataLoaderName}")

        return loader.load( env.getSource() )
    }
}


