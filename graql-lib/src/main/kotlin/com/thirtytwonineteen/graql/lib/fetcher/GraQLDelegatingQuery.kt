package com.thirtytwonineteen.graql.lib.fetcher

import com.google.gson.Gson
import com.thirtytwonineteen.graql.GraQLDelegate
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import java.lang.reflect.Method

interface GraQLDelegatingQuery<T>:DataFetcher<T>, GraQLDelegate {
    val name:String
    val argumentName: String
    val requestType: Class<*>

}

class DefaultGraQLDelegatingQuery(
    override val name:String,
    override val argumentName: String,
    override val requestType: Class<*>,
    val method: Method,
    val target: Any,
):GraQLDelegatingQuery<Any> {
    override fun get(env: DataFetchingEnvironment): Any? {
        val arg = env.getArgument<Any>(argumentName)

        val req = when {
            arg == null -> null
            else -> Gson().fromJson(Gson().toJson(arg), requestType)
        }
        val res = method.invoke( target, req )
        return res
    }
}
