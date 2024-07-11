package com.thirtytwonineteen.graql.lib.delegates

import com.thirtytwonineteen.graql.GraQLDelegate
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

interface GraQLDelegatingFetch<T>:DataFetcher<T>, GraQLDelegate {
    val type:String
    val field:String
    override fun get(env: DataFetchingEnvironment): T?
}

