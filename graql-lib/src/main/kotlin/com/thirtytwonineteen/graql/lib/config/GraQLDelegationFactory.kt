package com.thirtytwonineteen.graql.lib.config

import com.thirtytwonineteen.graql.*
import com.thirtytwonineteen.graql.lib.config.micronaut.GraQLDelegationException
import com.thirtytwonineteen.graql.lib.fetcher.*
import com.thirtytwonineteen.graql.lib.loader.DefaultGraQLDelegatingMappedBatchLoader
import com.thirtytwonineteen.graql.lib.loader.GraQLDelegatingMappedBatchLoader
import io.micronaut.context.BeanContext
import io.micronaut.inject.BeanDefinition
import io.micronaut.scheduling.TaskExecutors
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.lang.reflect.Method
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
    @Named("graQLMappedDataLoaderConfigurator") private val mappedDataLoaderConfigurator: GraQLDelegationConfigurator<GraQLMappedDataLoader>,
) : GraQLDelegationFactory {

    override val delegateConfigurators = mapOf(
        GraQLFetch::class to fetchConfigurator,
        GraQLQuery::class to queryConfigurator,
        GraQLMutation::class to mutationConfigurator,
        GraQLMappedDataLoader::class to mappedDataLoaderConfigurator,
    )

}

@Singleton @Named("graQLFetchConfigurator")
class DefaultGraQLFetchConfigurator(
    private val beanContext:BeanContext
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
    private val beanContext:BeanContext
) : GraQLDelegationConfigurator<GraQLQuery> {
    override fun createDelegate(beanDefinition: BeanDefinition<*>, method: Method, a: Annotation): List<GraQLDelegate> {
        val annotation = a as GraQLQuery
        if ( method.parameters.size != 1 ) {
            throw GraQLDelegationException("Cannot create GraQLQuery delegate for ${method.declaringClass.simpleName}::${method.name}: it does not require exactly one parameter.")
        }
        val requestParameter = method.parameters.first()

        return listOf(DefaultGraQLDelegatingQuery(
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
            requestType = requestParameter.type
        ))
    }
}

@Singleton @Named("graQLMutationConfigurator")
class DefaultGraQLMutationConfigurator(
    private val beanContext:BeanContext
) : GraQLDelegationConfigurator<GraQLMutation> {
    override fun createDelegate(beanDefinition: BeanDefinition<*>, method: Method, a: Annotation): List<GraQLDelegate> {
        val annotation = a as GraQLMutation
        if ( method.parameters.size != 1 ) {
            throw GraQLDelegationException("Cannot create GraQLMutation delegate for ${method.declaringClass.simpleName}::${method.name}: it does not require exactly one parameter.")
        }
        val requestParameter = method.parameters.first()

        return listOf(DefaultGraQLDelegatingMutation(
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
        ))
    }
}

@Singleton @Named("graQLMappedDataLoaderConfigurator")
class DefaultGraQLMappedDataLoaderConfigurator(
    private val beanContext:BeanContext,
    @Named(TaskExecutors.BLOCKING) var defaultExecutor: ExecutorService // <2>
) : GraQLDelegationConfigurator<GraQLMappedDataLoader> {
    override fun createDelegate(beanDefinition: BeanDefinition<*>, method: Method, a: Annotation): List<GraQLDelegate> {
        val annotation = a as GraQLMappedDataLoader
        if ( method.parameters.size != 1 ) {
            throw GraQLDelegationException("Cannot create GraQLDelegatingMappedBatchLoader delegate for ${method.declaringClass.simpleName}::${method.name}: it does not require exactly one parameter.")
        }

        return listOf(DefaultGraQLDelegatingMappedBatchLoader(
            dataLoaderName = when {
                annotation.name.isBlank() -> method.name
                else -> annotation.name
            },
            method = method,
            target = beanContext.getBean( beanDefinition ),
            executor = defaultExecutor,
        ))
    }
}

