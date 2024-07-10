package com.thirtytwonineteen.graql.lib.federation

import com.apollographql.federation.graphqljava._Entity
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.TypeResolver
import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Factory
import jakarta.inject.Named
import java.util.stream.Collectors

@Factory
class GraQLFederationProvider(
    private val beanContext: BeanContext,
) {

    val federatedEntityResolvers:Collection<GraQLFederatedEntityResolver<*>> by lazy {
        beanContext.getBeansOfType(GraQLFederatedEntityResolver::class.java)
    }

    val federatedEntityResolversByEntityType:Map<Class<*>, GraQLFederatedEntityResolver<*>> by lazy {
        federatedEntityResolvers.associateBy { it.entityType }
    }

    val federatedEntityResolversByEntityName:Map<String, GraQLFederatedEntityResolver<*>> by lazy {
        federatedEntityResolvers.associateBy { it.entityName }
    }

    @Context
    @Named("graQLFederationDataFetcher")
    fun federationDataFetcher():DataFetcher<*> {
        // TODO: check this format for re-use
        return DataFetcher { env: DataFetchingEnvironment ->
            env.getArgument<List<Map<String, Any>>>(_Entity.argumentName)!!
                .stream()
                .map<Any?> { representation: Map<String, Any> ->
                    val resolver = federatedEntityResolversByEntityName.get(representation["__typename"])
                    when {
                        resolver != null -> return@map resolver.resolution( representation )
                        else -> null
                    }
                }
                .collect(Collectors.toList())
        }
    }

    @Context
    @Named("graQLFederationTypeResolver")
    fun federationTypeResolver():TypeResolver {
        return TypeResolver { env ->
            val resolver = federatedEntityResolversByEntityType.get( env.getObject<Any>()::class.java )
            when {
                resolver != null -> return@TypeResolver env.schema.getObjectType(resolver.entityName)
                else -> {
                    throw IllegalArgumentException("No entity type registered for ${env.getObject<Any>()}")
                }
            }
        }
    }

}