package com.thirtytwonineteen.graql.lib.config.micronaut

import com.thirtytwonineteen.graql.*
import com.thirtytwonineteen.graql.lib.config.GraQLDelegationFactory
import com.thirtytwonineteen.graql.lib.exceptions.GraQLExceptionTranslatorReference
import graphql.GraphqlErrorBuilder
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Context
import io.micronaut.inject.BeanDefinition
import io.micronaut.inject.qualifiers.Qualifiers
import java.lang.reflect.Method

@Context
class GraQLBeanScanner(
    private val beanContext: BeanContext,
    private val delegationFactory: GraQLDelegationFactory,
) {

    val graQLScalars:Set<GraphQLScalarType> by lazy {
        beanContext
            .getBeanDefinitions(Qualifiers.byStereotype(GraQLScalar::class.java))
            .fold( mutableSetOf() ) { acc, it ->
                val bean = beanContext.getBean(it)
                if ( !Coercing::class.java.isAssignableFrom( bean::class.java ) ) {
                    throw RuntimeException("Bean ${it.name} isn't a GraphQL Coercing: it can't be registered as a scalar!")
                }
                val annotation = bean::class.java.annotations.find{ it is GraQLScalar } as GraQLScalar

                val scalar = GraphQLScalarType.newScalar()
                    .name(
                        when {
                            annotation.name.isBlank() -> bean::class.simpleName
                            else -> annotation.name
                        }
                    )
                    .description( annotation.description )
                    .coercing( bean as Coercing<*,*>)
                    .build()

                acc.add( scalar )
                acc
            }
    }

    val exceptionTranslators:Map<Class<Throwable>, GraQLExceptionTranslatorReference> by lazy {
        delegatesByType.exceptionTranslators
    }

    val componentDefinitions:Collection<BeanDefinition<*>> by lazy {
        beanContext.getBeanDefinitions(Qualifiers.byStereotype(GraQLComponent::class.java))
    }

    fun <T> delegatesForType(clazz: Class<*> ):List<T>{
        return delegatesByType.findDelegate( clazz )
    }

    private val delegatesByType: BeanScanningResults by lazy {
        val results = BeanScanningResults()

        componentDefinitions
            .forEach{ beanDefinition ->
                beanDefinition.beanType.methods
                    .forEach { method: Method ->

                        delegationFactory.delegateConfigurators.forEach{ key, configurator ->
                            method.getAnnotationsByType( key.java ).forEach { annotation ->
                                // listOf( key, annotation )
                                configurator.createDelegate(beanDefinition, method, annotation).forEach{
                                    results.putDelegate( it )
                                }
                            }
                        }

                        method.getAnnotationsByType( GraQLExceptionHandler::class.java ).forEach { annotation ->
                            if ( method.parameters.size != 2 ) {
                                throw RuntimeException("${beanDefinition.beanType.simpleName}::${method.name} can't be used as an exception handler: it should have one parameter")
                            }
                            if ( !Throwable::class.java.isAssignableFrom( method.parameters.first().type ) ) {
                                throw RuntimeException("${beanDefinition.beanType.simpleName}::${method.name} can't be used as an exception handler: its first argument should be a type of Throwable")
                            }
                            val exceptionType = method.parameters.first().type as Class<Throwable>
                            val ref = GraQLExceptionTranslatorReference(
                                beanContext.getBean( beanDefinition ),
                                method
                            )
                            results.putExceptionTranslator( exceptionType, ref )
                        }
                    }
            }

        results
    }

    class BeanScanningResults{
        val delegatesByInterface = mutableMapOf<Class<*>, MutableList<Any>>()
        val exceptionTranslators = mutableMapOf<Class<Throwable>, GraQLExceptionTranslatorReference>()

        fun putDelegate(delegate: Any) {

            // What flavor are we?
            val graQLInterface = delegate::class.java.interfaces.find { it ->
                GraQLDelegate::class.java.isAssignableFrom( it )
            }
            if ( graQLInterface == null ) {
                throw RuntimeException("Whoa nelly. You've implemented something (${delegate::class.simpleName}) in GraQL where your delegate doesn't implement the necessary GraQLDelegate interface. It's marker: add it and you should be fine.")
            }

            when {
                !delegatesByInterface.containsKey( graQLInterface ) -> delegatesByInterface.put( graQLInterface, mutableListOf( delegate ) )
                else -> delegatesByInterface.get( graQLInterface )!!.add( delegate )
            }
        }
        fun <T> findDelegate(clazz:Class<*>):List<T>{
            val isItThere = delegatesByInterface.containsKey(clazz)
            return when {
                isItThere -> delegatesByInterface.get( clazz ) as List<T>
                else -> emptyList()
            }
        }

        fun putExceptionTranslator(clazz:Class<Throwable>, t:GraQLExceptionTranslatorReference) {
            exceptionTranslators.put( clazz, t )
        }
    }
}

class GraQLDelegationException(msg:String) : RuntimeException(msg)