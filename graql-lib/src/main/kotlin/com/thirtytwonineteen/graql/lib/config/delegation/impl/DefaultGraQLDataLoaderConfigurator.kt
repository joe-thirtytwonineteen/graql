package com.thirtytwonineteen.graql.lib.config.delegation.impl

import com.thirtytwonineteen.graql.GraQLDataLoader
import com.thirtytwonineteen.graql.GraQLDelegate
import com.thirtytwonineteen.graql.lib.config.delegation.GraQLDelegationConfigurator
import com.thirtytwonineteen.graql.lib.config.GraQLDelegationException
import com.thirtytwonineteen.graql.lib.delegates.impl.DefaultGraQLDelegatingBatchLoader
import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import io.micronaut.context.BeanContext
import io.micronaut.inject.BeanDefinition
import io.micronaut.scheduling.TaskExecutors
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.lang.reflect.Method
import java.util.concurrent.ExecutorService

@Singleton
@Named("graQLDataLoaderConfigurator")
class DefaultGraQLDataLoaderConfigurator(
    private val beanContext: BeanContext,
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
                target = beanContext.getBean(beanDefinition),
                executor = defaultExecutor,
            )
        )
    }
}