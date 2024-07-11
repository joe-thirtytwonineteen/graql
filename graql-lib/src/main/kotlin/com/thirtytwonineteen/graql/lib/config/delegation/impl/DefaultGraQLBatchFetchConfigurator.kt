package com.thirtytwonineteen.graql.lib.config.delegation.impl

import com.thirtytwonineteen.graql.GraQLDelegate
import com.thirtytwonineteen.graql.GraQLBatchFetch
import com.thirtytwonineteen.graql.lib.config.delegation.GraQLDelegationConfigurator
import com.thirtytwonineteen.graql.lib.config.GraQLDelegationException
import com.thirtytwonineteen.graql.lib.delegates.impl.DefaultGraQLDataLoaderFetch
import com.thirtytwonineteen.graql.lib.delegates.impl.DefaultGraQLDelegatingBatchLoader
import com.thirtytwonineteen.graql.lib.delegates.impl.DefaultGraQLDelegatingMappedBatchLoader
import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import io.micronaut.context.BeanContext
import io.micronaut.inject.BeanDefinition
import io.micronaut.scheduling.TaskExecutors
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.concurrent.ExecutorService

@Singleton
@Named("graQLBatchFetchConfigurator")
class DefaultGraQLBatchFetchConfigurator(
    private val beanContext: BeanContext,
    private val exceptionHandler: GraQLGlobalExceptionHandler,
    @Named(TaskExecutors.BLOCKING) var defaultExecutor: ExecutorService // <2>
) : GraQLDelegationConfigurator<GraQLBatchFetch> {
    override fun createDelegate(beanDefinition: BeanDefinition<*>, method: Method, a: Annotation): List<GraQLDelegate> {

        val annotation = a as GraQLBatchFetch

        if ( method.parameters.size != 1 ) {
            throw GraQLDelegationException("Cannot create GraQLMappedBatchFetch delegate for ${method.declaringClass.simpleName}::${method.name}: it does not require exactly one parameter.")
        }

        val requestParameter = method.parameters.first()

        var type = annotation.type
        if ( type.isBlank() ) {
            val requestParameterType = requestParameter.type
            if ( !Collection::class.java.isAssignableFrom( requestParameterType ) ) {
                throw GraQLDelegationException("Cannot create GraQLMappedBatchFetch delegate for ${method.declaringClass.simpleName}::${method.name}: its first parameter is not a collection. You may need to provide a 'type' value in your annotation.")
            }
            val parameterizedType = requestParameter.parameterizedType
            if ( parameterizedType !is ParameterizedType) {
                throw GraQLDelegationException("Cannot create GraQLMappedBatchFetch delegate for ${method.declaringClass.simpleName}::${method.name}: its first parameter is not a collection of a parameterized type. You may need to provide a 'type' value in your annotation.")
            }
            if ( parameterizedType.actualTypeArguments.isEmpty() ) {
                throw GraQLDelegationException("Cannot create GraQLMappedBatchFetch delegate for ${method.declaringClass.simpleName}::${method.name}: its first parameter has no actual type declared. You may need to provide a 'type' value in your annotation.")
            }
            val firstActualType = parameterizedType.actualTypeArguments.first() as Class<*>
            type = firstActualType.simpleName
        }

        val useMappedLoader = when {
            (Map::class.java.isAssignableFrom( method.returnType )) -> true
            (List::class.java.isAssignableFrom( method.returnType )) -> false
            else -> {
                throw GraQLDelegationException("Cannot create GraQLMappedBatchFetch delegate for ${method.declaringClass.simpleName}::${method.name}: it does not return a list or map.")
            }
        }

        val field = when {
            annotation.field.isBlank() -> method.name
            else -> annotation.field
        }
        val dataLoaderName = when {
            annotation.dataLoaderName.isBlank() -> "${field}DataLoader"
            else -> annotation.dataLoaderName
        }

        // produce _two_ delegates:
        // a fetch delegate that does:
        // return dfe.getDataLoader<Any, Any>("${methodName}DataLoader")!!.load(  *THING* )
        val fetcher = DefaultGraQLDataLoaderFetch(
            type = type,
            field = field,
            dataLoaderName = dataLoaderName,
            exceptionHandler = exceptionHandler,
        )

        // a conventionally-named data loader that actually invokes this method...
        val loader = when {
            useMappedLoader -> DefaultGraQLDelegatingMappedBatchLoader(
                exceptionHandler = exceptionHandler,
                dataLoaderName = dataLoaderName,
                method = method,
                target = beanContext.getBean(beanDefinition),
                executor = defaultExecutor,
            )
            else -> DefaultGraQLDelegatingBatchLoader(
                exceptionHandler = exceptionHandler,
                dataLoaderName = dataLoaderName,
                method = method,
                target = beanContext.getBean(beanDefinition),
                executor = defaultExecutor,
            )
        }

        return listOf( fetcher, loader )
    }
}