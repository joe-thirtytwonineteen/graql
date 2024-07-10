package com.thirtytwonineteen.graql.lib.config

import graphql.GraphQL
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.core.io.ResourceResolver
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader

@Factory
class GraQLFactory {

    companion object {
        private val LOG = LoggerFactory.getLogger(GraQLFactory::class.java)
    }

    @Singleton
    fun graphQL(
        resourceResolver: ResourceResolver,
        graQLConfigurationProperties: GraQLConfigurationProperties,
        graQLRuntimeWirer: GraQLRuntimeWirer
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


            val schemaGenerator = SchemaGenerator()
            val graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring)

            GraphQL
                .newGraphQL(graphQLSchema)
                .build()

        } else {
            throw RuntimeException(
                "No GraphQL services found, not creating GraphQL bean: creating an empty schema will throw an even more vague exception than this. Check your configured schema locations (${graQLConfigurationProperties.schemaLocations}) to make sure schema files exist."
            )
        }
    }

}