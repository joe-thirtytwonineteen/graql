package com.thirtytwonineteen.graql.lib.config.micronaut

import com.thirtytwonineteen.graql.*
import com.thirtytwonineteen.graql.lib.config.GraQLDelegationFactory
import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Context
import io.micronaut.inject.BeanDefinition
import io.micronaut.inject.qualifiers.Qualifiers
import java.lang.reflect.Method
import kotlin.reflect.KClass

@Context
class GraQLBeanScanner(
    private val beanContext: BeanContext,
    private val delegationFactory: GraQLDelegationFactory,
) {

    val componentDefinitions:Collection<BeanDefinition<*>> by lazy {
        beanContext.getBeanDefinitions(Qualifiers.byStereotype(GraQLComponent::class.java))
    }

    fun <T> delegatesForType( clazz: KClass<*> ):List<T>{
        return delegatesByType.forType( clazz )
    }

    private val delegatesByType: DelegatesByType by lazy {
        val byType = DelegatesByType()

        componentDefinitions
            .forEach{ beanDefinition ->
                delegationFactory.delegateConfigurators.forEach{ key, configurator ->
                    beanDefinition.beanType.methods
                        .forEach { method: Method ->
                            method.getAnnotationsByType( key ).forEach { annotation ->
                                listOf( key, annotation )
                                val d = configurator.createDelegate(beanDefinition, method, annotation)!!
                                byType.putByType( d::class, d )
                            }
                        }
                }
            }

        byType
    }

    private class DelegatesByType{
        val byType = mutableMapOf<KClass<*>, MutableList<Any>>()
        fun putByType(clazz: KClass<*>, delegate: Any ) {
            when {
                !byType.containsKey( clazz ) -> byType.put( clazz, mutableListOf( delegate ) )
                else -> byType.get( clazz )!!.add( delegate )
            }
        }
        fun <T> forType( clazz: KClass<*> ):List<T>{
            val isItThere = byType.containsKey(clazz)
            return when {
                isItThere -> byType.get( clazz ) as List<T>
                else -> emptyList()
            }
        }
    }
}

class GraQLDelegationException(msg:String) : RuntimeException(msg)