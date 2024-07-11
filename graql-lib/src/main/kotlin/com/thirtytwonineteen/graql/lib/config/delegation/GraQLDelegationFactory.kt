package com.thirtytwonineteen.graql.lib.config.delegation

import kotlin.reflect.KClass

open interface GraQLDelegationFactory {

    val delegateConfigurators: Map<KClass<out Annotation>, GraQLDelegationConfigurator<*>>

}