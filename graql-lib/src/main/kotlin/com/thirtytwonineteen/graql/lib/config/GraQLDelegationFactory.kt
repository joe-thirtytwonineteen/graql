package com.thirtytwonineteen.graql.lib.config

import DefaultGraQLDelegatingFetch
import com.thirtytwonineteen.graql.*
import com.thirtytwonineteen.graql.lib.config.micronaut.GraQLDelegationException
import com.thirtytwonineteen.graql.lib.delegates.*
import com.thirtytwonineteen.graql.lib.delegates.impl.*
import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import com.thirtytwonineteen.graql.lib.mapping.GraQLRequestParameterMapper
import io.micronaut.context.BeanContext
import io.micronaut.inject.BeanDefinition
import io.micronaut.scheduling.TaskExecutors
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.concurrent.ExecutorService
import kotlin.reflect.KClass

open interface GraQLDelegationFactory {

    val delegateConfigurators: Map<KClass<out Annotation>, GraQLDelegationConfigurator<*>>

}

interface GraQLDelegationConfigurator<ANNOTATION>{
    fun createDelegate(beanDefinition: BeanDefinition<*>, method: Method, annotation: Annotation):List<GraQLDelegate>
}

@Singleton @Named("graQLDelegationFactory")
open class DefaultGraQLDelegationFactory(
    @Named("graQLFetchConfigurator") private val fetchConfigurator: GraQLDelegationConfigurator<GraQLFetch>,
    @Named("graQLQueryConfigurator") private val queryConfigurator: GraQLDelegationConfigurator<GraQLQuery>,
    @Named("graQLMutationConfigurator") private val mutationConfigurator: GraQLDelegationConfigurator<GraQLMutation>,
    @Named("graQLDataLoaderConfigurator") private val dataLoaderConfigurator: GraQLDelegationConfigurator<GraQLDataLoader>,
    @Named("graQLMappedDataLoaderConfigurator") private val mappedDataLoaderConfigurator: GraQLDelegationConfigurator<GraQLMappedDataLoader>,
    @Named("graQLMappedBatchFetchConfigurator") private val mappedBatchFetchConfigurator: GraQLDelegationConfigurator<GraQLMappedBatchFetch>,
) : GraQLDelegationFactory {

    override val delegateConfigurators = mapOf(
        GraQLFetch::class to fetchConfigurator,
        GraQLQuery::class to queryConfigurator,
        GraQLMutation::class to mutationConfigurator,
        GraQLDataLoader::class to dataLoaderConfigurator,
        GraQLMappedDataLoader::class to mappedDataLoaderConfigurator,
        GraQLMappedBatchFetch::class to mappedBatchFetchConfigurator,
    )

}

@Singleton @Named("graQLFetchConfigurator")
class DefaultGraQLFetchConfigurator(
    private val beanContext:BeanContext,
    private val exceptionHandler: GraQLGlobalExceptionHandler,
) : GraQLDelegationConfigurator<GraQLFetch> {
    override fun createDelegate(beanDefinition: BeanDefinition<*>, method: Method, a: Annotation): List<GraQLDelegate> {
        val annotation = a as GraQLFetch
        /*
        if (method.parameters.size != 1) {
            throw GraQLDelegationException("Cannot create GraQLFetch delegate for ${method.declaringClass.simpleName}::${method.name}: it does not require exactly one parameter.")
        }
        */
        val requestParameter = method.parameters.first()

        return listOf(DefaultGraQLDelegatingFetch(
            exceptionHandler = exceptionHandler,
            type = when {
                annotation.type.isBlank() -> requestParameter.type.simpleName
                else -> annotation.type
            },
            field = when {
                annotation.field.isBlank() -> method.name
                else -> annotation.field
            },
            method = method,
            target = beanContext.getBean(beanDefinition),
        ))
    }
}

@Singleton @Named("graQLQueryConfigurator")
class DefaultGraQLQueryConfigurator(
    private val beanContext:BeanContext,
    private val parameterMapper: GraQLRequestParameterMapper,
    private val exceptionHandler: GraQLGlobalExceptionHandler,
) : GraQLDelegationConfigurator<GraQLQuery> {
    override fun createDelegate(beanDefinition: BeanDefinition<*>, method: Method, a: Annotation): List<GraQLDelegate> {
        val annotation = a as GraQLQuery
        if ( method.parameters.size != 1 ) {
            throw GraQLDelegationException("Cannot create GraQLQuery delegate for ${method.declaringClass.simpleName}::${method.name}: it does not require exactly one parameter.")
        }
        val requestParameter = method.parameters.first()

        return listOf(
            DefaultGraQLDelegatingQuery(
                exceptionHandler = exceptionHandler,
                name = when {
                    annotation.name.isBlank() -> method.name
                    else -> annotation.name
                },
                method = method,
                target = beanContext.getBean( beanDefinition ),
                argumentName = when {
                    annotation.input.isBlank() -> requestParameter.name
                    else -> annotation.input
                },
                requestType = requestParameter.type,
                requestParameterMapper = parameterMapper
            )
        )
    }
}

@Singleton @Named("graQLMutationConfigurator")
class DefaultGraQLMutationConfigurator(
    private val beanContext:BeanContext,
    private val parameterMapper: GraQLRequestParameterMapper,
    private val exceptionHandler: GraQLGlobalExceptionHandler,
) : GraQLDelegationConfigurator<GraQLMutation> {
    override fun createDelegate(beanDefinition: BeanDefinition<*>, method: Method, a: Annotation): List<GraQLDelegate> {
        val annotation = a as GraQLMutation
        if ( method.parameters.size != 1 ) {
            throw GraQLDelegationException("Cannot create GraQLMutation delegate for ${method.declaringClass.simpleName}::${method.name}: it does not require exactly one parameter.")
        }
        val requestParameter = method.parameters.first()

        return listOf(
            DefaultGraQLDelegatingMutation(
                exceptionHandler = exceptionHandler,
                name = when {
                    annotation.name.isBlank() -> method.name
                    else -> annotation.name
                },
                method = method,
                target = beanContext.getBean( beanDefinition ),
                argumentName = when {
                    annotation.input.isBlank() -> "input"
                    else -> annotation.input
                },
                requestType = requestParameter.type,
                requestParameterMapper = parameterMapper
            )
        )
    }
}

@Singleton @Named("graQLDataLoaderConfigurator")
class DefaultGraQLDataLoaderConfigurator(
    private val beanContext:BeanContext,
    private val exceptionHandler: GraQLGlobalExceptionHandler,
    @Named(TaskExecutors.BLOCKING) var defaultExecutor: ExecutorService // <2>
) : GraQLDelegationConfigurator<GraQLDataLoader> {
    override fun createDelegate(beanDefinition: BeanDefinition<*>, method: Method, a: Annotation): List<GraQLDelegate> {
        val annotation = a as GraQLDataLoader
        if ( method.parameters.size != 1 ) {
            throw GraQLDelegationException("Cannot create GraQLDelegatingBatchLoader delegate for ${method.declaringClass.simpleName}::${method.name}: it does not require exactly one parameter.")
        }

        return listOf(
            DefaultGraQLDelegatingBatchLoader(
                exceptionHandler = exceptionHandler,
                dataLoaderName = when {
                    annotation.name.isBlank() -> method.name
                    else -> annotation.name
                },
                method = method,
                target = beanContext.getBean( beanDefinition ),
                executor = defaultExecutor,
            )
        )
    }
}

@Singleton @Named("graQLMappedDataLoaderConfigurator")
class DefaultGraQLMappedDataLoaderConfigurator(
    private val beanContext:BeanContext,
    private val exceptionHandler: GraQLGlobalExceptionHandler,
    @Named(TaskExecutors.BLOCKING) var defaultExecutor: ExecutorService // <2>
) : GraQLDelegationConfigurator<GraQLMappedDataLoader> {
    override fun createDelegate(beanDefinition: BeanDefinition<*>, method: Method, a: Annotation): List<GraQLDelegate> {
        val annotation = a as GraQLMappedDataLoader
        if ( method.parameters.size != 1 ) {
            throw GraQLDelegationException("Cannot create GraQLDelegatingMappedBatchLoader delegate for ${method.declaringClass.simpleName}::${method.name}: it does not require exactly one parameter.")
        }

        return listOf(
            DefaultGraQLDelegatingMappedBatchLoader(
                exceptionHandler = exceptionHandler,
                dataLoaderName = when {
                    annotation.name.isBlank() -> method.name
                    else -> annotation.name
                },
                method = method,
                target = beanContext.getBean( beanDefinition ),
                executor = defaultExecutor,
            )
        )
    }
}

@Singleton @Named("graQLMappedBatchFetchConfigurator")
class DefaultGraQLMappedBatchFetchConfigurator(
    private val beanContext:BeanContext,
    private val exceptionHandler: GraQLGlobalExceptionHandler,
    @Named(TaskExecutors.BLOCKING) var defaultExecutor: ExecutorService // <2>
) : GraQLDelegationConfigurator<GraQLMappedBatchFetch> {
    override fun createDelegate(beanDefinition: BeanDefinition<*>, method: Method, a: Annotation): List<GraQLDelegate> {

        val annotation = a as GraQLMappedBatchFetch

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
            if ( parameterizedType !is ParameterizedType ) {
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
        val fetcher =  DefaultGraQLDataLoaderFetch(
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
                target = beanContext.getBean( beanDefinition ),
                executor = defaultExecutor,
            )
            else -> DefaultGraQLDelegatingBatchLoader(
                exceptionHandler = exceptionHandler,
                dataLoaderName = dataLoaderName,
                method = method,
                target = beanContext.getBean( beanDefinition ),
                executor = defaultExecutor,
            )
        }

        return listOf( fetcher, loader )
    }
}
