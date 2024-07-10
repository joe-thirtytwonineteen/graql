package com.thirtytwonineteen.graql.lib.fetcher

import com.google.gson.Gson
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import java.lang.reflect.Method

class GraQLDelegatingMutation(
    val name:String,
    val method: Method,
    val target: Any,
    val argumentName: String,
    val requestType: Class<*>,
):DataFetcher<Any> {
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
