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

    fun <T> delegatesForAnnotation(clazz: KClass<out Annotation> ):List<T>{
        return delegatesByType.find( clazz )
    }

    private val delegatesByType: DelegatesByAnnotation by lazy {
        val byType = DelegatesByAnnotation()

        componentDefinitions
            .forEach{ beanDefinition ->
                delegationFactory.delegateConfigurators.forEach{ key, configurator ->
                    beanDefinition.beanType.methods
                        .forEach { method: Method ->
                            method.getAnnotationsByType( key.java ).forEach { annotation ->
                                listOf( key, annotation )
                                val d = configurator.createDelegate(beanDefinition, method, annotation)!!
                                byType.put( key, d )
                            }
                        }
                }
            }

        byType
    }

    private class DelegatesByAnnotation{
        val byAnnotation = mutableMapOf<Any, MutableList<Any>>()
        fun put(annotation:KClass<out Annotation>, delegate: Any ) {
            when {
                !byAnnotation.containsKey( annotation ) -> byAnnotation.put( annotation, mutableListOf( delegate ) )
                else -> byAnnotation.get( annotation )!!.add( delegate )
            }
        }
        fun <T> find(annotation:KClass<out Annotation> ):List<T>{
            val isItThere = byAnnotation.containsKey(annotation)
            return when {
                isItThere -> byAnnotation.get( annotation ) as List<T>
                else -> emptyList()
            }
        }
    }
}

class GraQLDelegationException(msg:String) : RuntimeException(msg)