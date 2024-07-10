package com.thirtytwonineteen.graql.lib.fetcher

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import java.lang.reflect.Method

class GraQLDelegatingFetch(
    val type: String,
    val field:String,
    val method: Method,
    val target: Any,
): DataFetcher<Any> {
    override fun get(env: DataFetchingEnvironment): Any? {
        val res = method.invoke( target, env.getSource() )
        return res
    }
}