package com.thirtytwonineteen.graql.lib.fetcher

import com.thirtytwonineteen.graql.GraQLDelegate
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import java.lang.reflect.Method

interface GraQLDelegatingFetch<T>:DataFetcher<T>, GraQLDelegate {
    val type:String
    val field:String
    override fun get(env: DataFetchingEnvironment): T?
}

class DefaultGraQLDelegatingFetch(
    override val type: String,
    override val field:String,
    val method: Method,
    val target: Any,
): GraQLDelegatingFetch<Any> {
    override fun get(env: DataFetchingEnvironment): Any? {
        val res = when {
            method.parameters.size == 1 -> method.invoke( target, env.getSource() )
            else -> method.invoke( target, env.getSource(), env )
        }
        return res
    }
}
