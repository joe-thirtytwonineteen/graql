package com.thirtytwonineteen.graql.lib.fetcher

import com.google.gson.Gson
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import java.lang.reflect.Method

interface GraQLDelegatingMutation<T>:DataFetcher<T> {
    val name:String
    val argumentName: String
    val requestType: Class<*>

    override fun get(env: DataFetchingEnvironment): T?
}

class DefaultGraQLDelegatingMutation(
    override val name:String,
    override val argumentName: String,
    override val requestType: Class<*>,
    val method: Method,
    val target: Any,
):GraQLDelegatingMutation<Any> {
    override fun get(env: DataFetchingEnvironment): Any? {
        val arg = env.getArgument<Map<Any, Any>?>(argumentName)

        val req = when {
            arg == null -> null
            else -> Gson().fromJson(Gson().toJson(arg), requestType)
        }
        val res = method.invoke( target, req )
        return res
    }
}
