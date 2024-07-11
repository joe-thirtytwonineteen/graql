package com.thirtytwonineteen.graql.lib.config.delegation.impl

import com.thirtytwonineteen.graql.GraQLDelegate
import com.thirtytwonineteen.graql.GraQLMutation
import com.thirtytwonineteen.graql.lib.config.delegation.GraQLDelegationConfigurator
import com.thirtytwonineteen.graql.lib.config.GraQLDelegationException
import com.thirtytwonineteen.graql.lib.delegates.impl.DefaultGraQLDelegatingMutation
import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import com.thirtytwonineteen.graql.lib.mapping.GraQLRequestParameterMapper
import io.micronaut.context.BeanContext
import io.micronaut.inject.BeanDefinition
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.lang.reflect.Method

@Singleton
@Named("graQLMutationConfigurator")
class DefaultGraQLMutationConfigurator(
    private val beanContext: BeanContext,
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
                target = beanContext.getBean(beanDefinition),
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