package com.thirtytwonineteen.graql.lib.config.delegation

import com.thirtytwonineteen.graql.*
import io.micronaut.inject.BeanDefinition
import java.lang.reflect.Method

interface GraQLDelegationConfigurator<ANNOTATION>{
    fun createDelegate(beanDefinition: BeanDefinition<*>, method: Method, annotation: Annotation):List<GraQLDelegate>
}

