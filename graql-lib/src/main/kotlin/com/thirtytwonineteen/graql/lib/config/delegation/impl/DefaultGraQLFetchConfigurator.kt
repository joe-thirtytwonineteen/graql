package com.thirtytwonineteen.graql.lib.config.delegation.impl

import DefaultGraQLDelegatingFetch
import com.thirtytwonineteen.graql.GraQLDelegate
import com.thirtytwonineteen.graql.GraQLFetch
import com.thirtytwonineteen.graql.lib.config.delegation.GraQLDelegationConfigurator
import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import io.micronaut.context.BeanContext
import io.micronaut.inject.BeanDefinition
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.lang.reflect.Method

@Singleton
@Named("graQLFetchConfigurator")
class DefaultGraQLFetchConfigurator(
    private val beanContext: BeanContext,
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

        return listOf(
            DefaultGraQLDelegatingFetch(
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
            )
        )
    }
}