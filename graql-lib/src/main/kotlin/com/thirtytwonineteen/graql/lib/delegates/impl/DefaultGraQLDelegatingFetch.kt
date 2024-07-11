import com.thirtytwonineteen.graql.lib.delegates.GraQLDelegatingFetch
import com.thirtytwonineteen.graql.lib.delegates.impl.AbstractGraQLDelegate
import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import graphql.schema.DataFetchingEnvironment
import java.lang.reflect.Method

class DefaultGraQLDelegatingFetch(
    override val type: String,
    override val field:String,
    val method: Method,
    val target: Any,
    exceptionHandler: GraQLGlobalExceptionHandler
): GraQLDelegatingFetch<Any>, AbstractGraQLDelegate(exceptionHandler) {

    override fun get(env: DataFetchingEnvironment): Any? {
        return withExceptionHandling(
            {
                when {
                    method.parameters.size == 1 -> method.invoke( target, env.getSource() )
                    else -> method.invoke( target, env.getSource(), env )
                }
            },
            { "Error delegating to ${target::class.simpleName}::${method.name}: your client should get an error message. Exception is logged." }
        )
    }
}
