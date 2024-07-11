package com.thirtytwonineteen.graql.lib.config

import com.thirtytwonineteen.graql.GraQL
import com.thirtytwonineteen.graql.lib.config.micronaut.GraQLBeanScanner
import graphql.schema.idl.RuntimeWiring
import jakarta.inject.Singleton

import com.thirtytwonineteen.graql.lib.fetcher.GraQLDelegatingFetch
import com.thirtytwonineteen.graql.lib.fetcher.GraQLDelegatingMutation
import com.thirtytwonineteen.graql.lib.fetcher.GraQLDelegatingQuery
import com.thirtytwonineteen.graql.lib.loader.GraQLDelegatingMappedBatchLoader
import graphql.execution.instrumentation.SimpleInstrumentationContext
import graphql.schema.idl.*
import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry

@Singleton
open class GraQLRuntimeWirer(
    private val graQL: GraQL
) {

    fun wire( builder: RuntimeWiring.Builder): RuntimeWiring.Builder {

        graQL.scalars.forEach {
            builder.scalar( it )
        }

        builder
            .type("Query") { typeWiring: TypeRuntimeWiring.Builder ->  // <3>
                graQL.queries.forEach {
                    typeWiring.dataFetcher( it.name, it )
                }
                typeWiring
            }
            .type("Mutation") { typeWiring: TypeRuntimeWiring.Builder ->  // <4>
                graQL.mutations.forEach {
                    typeWiring.dataFetcher( it.name, it )
                }
                typeWiring
            }

        graQL.fetches
            .groupBy { it.type }
            .forEach { (type, delegates) ->
                builder.type( type ) { typeWiring ->
                    delegates.forEach{
                        typeWiring.dataFetcher( it.field, it )
                    }
                    typeWiring
                }
            }

        return builder
    }
}