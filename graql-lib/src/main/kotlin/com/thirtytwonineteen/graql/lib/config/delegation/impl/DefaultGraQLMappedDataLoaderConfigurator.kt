package com.thirtytwonineteen.graql.lib.config.delegation.impl

import com.thirtytwonineteen.graql.GraQLDelegate
import com.thirtytwonineteen.graql.GraQLMappedDataLoader
import com.thirtytwonineteen.graql.lib.config.delegation.GraQLDelegationConfigurator
import com.thirtytwonineteen.graql.lib.config.GraQLDelegationException
import com.thirtytwonineteen.graql.lib.delegates.impl.DefaultGraQLMappedBatchLoaderFactory
import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import io.micronaut.context.BeanContext
import io.micronaut.inject.BeanDefinition
import io.micronaut.scheduling.TaskExecutors
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.lang.reflect.Method
import java.util.concurrent.ExecutorService

@Singleton
@Named("graQLMappedDataLoaderConfigurator")
class DefaultGraQLMappedDataLoaderConfigurator(
    private val beanContext: BeanContext,
    private val exceptionHandler: GraQLGlobalExceptionHandler,
    @Named(TaskExecutors.BLOCKING) var defaultExecutor: ExecutorService // <2>
) : GraQLDelegationConfigurator<GraQLMappedDataLoader> {
    override fun createDelegate(beanDefinition: BeanDefinition<*>, method: Method, a: Annotation): List<GraQLDelegate> {
        val annotation = a as GraQLMappedDataLoader
        if ( method.parameters.size != 1 ) {
            throw GraQLDelegationException("Cannot create GraQLDelegatingMappedBatchLoader delegate for ${method.declaringClass.simpleName}::${method.name}: it does not require exactly one parameter.")
        }

        return listOf(
            DefaultGraQLMappedBatchLoaderFactory(
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