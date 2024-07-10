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

    fun <T> delegatesForAnnotation(clazz: Class<*> ):List<T>{
        return delegatesByType.find( clazz )
    }

    private val delegatesByType: DelegatesByInterface by lazy {
        val byInterface = DelegatesByInterface()

        componentDefinitions
            .forEach{ beanDefinition ->
                delegationFactory.delegateConfigurators.forEach{ key, configurator ->
                    beanDefinition.beanType.methods
                        .forEach { method: Method ->
                            method.getAnnotationsByType( key.java ).forEach { annotation ->
                                listOf( key, annotation )
                                configurator.createDelegate(beanDefinition, method, annotation).forEach{
                                    byInterface.put( it )
                                }
                            }
                        }
                }
            }

        byInterface
    }

    class DelegatesByInterface{
        val byInterface = mutableMapOf<Class<*>, MutableList<Any>>()

        fun put(delegate: Any) {

            // What flavor are we?
            val graQLInterface = delegate::class.java.interfaces.find { it ->
                GraQLDelegate::class.java.isAssignableFrom( it )
            }
            if ( graQLInterface == null ) {
                throw RuntimeException("Whoa nelly. You've implemented something (${delegate::class.simpleName}) in GraQL where your delegate doesn't implement the necessary GraQLDelegate interface. It's marker: add it and you should be fine.")
            }

            when {
                !byInterface.containsKey( graQLInterface ) -> byInterface.put( graQLInterface, mutableListOf( delegate ) )
                else -> byInterface.get( graQLInterface )!!.add( delegate )
            }
        }
        fun <T> find( clazz:Class<*> ):List<T>{
            val isItThere = byInterface.containsKey(clazz)
            return when {
                isItThere -> byInterface.get( clazz ) as List<T>
                else -> emptyList()
            }
        }
    }
}

class GraQLDelegationException(msg:String) : RuntimeException(msg)