package com.thirtytwonineteen.graql.lib.config.delegation.impl

import com.thirtytwonineteen.graql.GraQLDelegate
import com.thirtytwonineteen.graql.GraQLQuery
import com.thirtytwonineteen.graql.lib.config.delegation.GraQLDelegationConfigurator
import com.thirtytwonineteen.graql.lib.config.GraQLDelegationException
import com.thirtytwonineteen.graql.lib.delegates.impl.DefaultGraQLDelegatingQuery
import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import com.thirtytwonineteen.graql.lib.mapping.GraQLRequestParameterMapper
import io.micronaut.context.BeanContext
import io.micronaut.inject.BeanDefinition
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.lang.reflect.Method

@Singleton
@Named("graQLQueryConfigurator")
class DefaultGraQLQueryConfigurator(
    private val beanContext: BeanContext,
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
                target = beanContext.getBean(beanDefinition),
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