package com.thirtytwonineteen.graql.lib.delegates.impl

import com.thirtytwonineteen.graql.lib.delegates.GraQLDelegatingQuery
import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import com.thirtytwonineteen.graql.lib.mapping.GraQLRequestParameterMapper
import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory
import java.lang.reflect.Method

class DefaultGraQLDelegatingQuery(
    override val name:String,
    override val argumentName: String,
    override val requestType: Class<*>,
    val requestParameterMapper: GraQLRequestParameterMapper,
    val method: Method,
    val target: Any,
    exceptionHandler: GraQLGlobalExceptionHandler,
): GraQLDelegatingQuery<Any>, AbstractGraQLDelegate(exceptionHandler) {

    override fun get(env: DataFetchingEnvironment): Any? {
        val arg = env.getArgument<Any>(argumentName)

        val req = when {
            arg == null -> null
            else -> requestParameterMapper.map( arg, requestType )
        }

        return withExceptionHandling(
            {
                method.invoke( target, req )
            },
            { "Error delegating to ${target::class.simpleName}::${method.name}: your client should get an error message. Exception is logged." }
        )
    }
}