package com.thirtytwonineteen.graql.lib.config

import com.apollographql.federation.graphqljava.Federation
import com.thirtytwonineteen.graql.GraQL
import com.thirtytwonineteen.graql.lib.config.properties.GraQLConfigurationProperties
import com.thirtytwonineteen.graql.lib.exceptions.GraQLGlobalExceptionHandler
import graphql.GraphQL
import graphql.execution.preparsed.PreparsedDocumentProvider
import graphql.schema.DataFetcher
import graphql.schema.TypeResolver
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Factory
import io.micronaut.core.io.ResourceResolver
import io.micronaut.runtime.http.scope.RequestScope
import jakarta.inject.Singleton
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import java.io.BufferedReader
import java.io.InputStreamReader


@Factory
class GraQLFactory {

    @Singleton
    fun graphQL(
        beanContext: BeanContext,
        resourceResolver: ResourceResolver,
        graQLConfigurationProperties: GraQLConfigurationProperties,
        graQLRuntimeWirer: GraQLRuntimeWirer,
        graQLGlobalExceptionHandler: GraQLGlobalExceptionHandler,
        graQLFederationDataFetcher: DataFetcher<*>,
        graQLFederationTypeResolver: TypeResolver
    ): GraphQL? {
        val schemaParser = SchemaParser() // <2>

        val typeRegistry = TypeDefinitionRegistry()

        val schemas = graQLConfigurationProperties.schemaLocations
            .map { resourceResolver.getResourceAsStream(it) }
            .filter { it.isPresent }

        return if (schemas.isNotEmpty()) {
            schemas.forEach {
                typeRegistry.merge(schemaParser.parse(BufferedReader(InputStreamReader(it.get()))))
            }

            val runtimeWiring = graQLRuntimeWirer
                .wire( RuntimeWiring.newRuntimeWiring() )
                .build()


            // val schemaGenerator = SchemaGenerator()
            // val graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring)

            val graphQLSchema = Federation.transform(typeRegistry, runtimeWiring)
                .fetchEntities(graQLFederationDataFetcher)
                .resolveEntityType(graQLFederationTypeResolver)
                .build()

            val builder = GraphQL.newGraphQL(graphQLSchema)
                .defaultDataFetcherExceptionHandler(graQLGlobalExceptionHandler)

            if ( beanContext.containsBean( PreparsedDocumentProvider::class.java ) ) {
                builder.preparsedDocumentProvider( beanContext.getBean( PreparsedDocumentProvider::class.java ) )
            }


            builder.build()
        } else {
            throw RuntimeException(
                "No GraphQL services found, not creating GraphQL bean: creating an empty schema will throw an even more vague exception than this. Check your configured schema locations (${graQLConfigurationProperties.schemaLocations}) to make sure schema files exist."
            )
        }
    }

    @Suppress("unused")
    @RequestScope
    open fun dataLoaderRegistry(
        graQL: GraQL
    ): DataLoaderRegistry {
        val dataLoaderRegistry = DataLoaderRegistry()

        graQL.batchLoaders.forEach{
            dataLoaderRegistry.register( it.dataLoaderName, DataLoader.newDataLoader(it) )
        }
        graQL.mappedBatchLoaders.forEach{
            dataLoaderRegistry.register( it.dataLoaderName, DataLoader.newMappedDataLoader(it) )
        }

        return dataLoaderRegistry
    }

}