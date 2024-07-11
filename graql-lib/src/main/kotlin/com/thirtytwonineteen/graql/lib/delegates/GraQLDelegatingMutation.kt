package com.thirtytwonineteen.graql.lib.delegates

import com.thirtytwonineteen.graql.GraQLDelegate
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

interface GraQLDelegatingMutation<T>:DataFetcher<T>, GraQLDelegate {
    val name:String
    val argumentName: String
    val requestType: Class<*>

    override fun get(env: DataFetchingEnvironment): T?
}

