package com.thirtytwonineteen.graql

import com.thirtytwonineteen.graql.lib.config.GraQLComponentScanner
import com.thirtytwonineteen.graql.lib.delegates.*
import graphql.schema.GraphQLScalarType
import jakarta.inject.Singleton

@Singleton
class GraQL(
    private val graQLComponentScanner: GraQLComponentScanner
) {

    val scalars:Set<GraphQLScalarType> get() {
        return graQLComponentScanner.graQLScalars
    }

    val queries:List<GraQLDelegatingQuery<Any>> get() {
        return graQLComponentScanner.delegatesForType(GraQLDelegatingQuery::class.java)
    }

    val mutations:List<GraQLDelegatingMutation<Any>> get() {
        return graQLComponentScanner.delegatesForType(GraQLDelegatingMutation::class.java)
    }

    val fetches:List<GraQLDelegatingFetch<Any>> get() {
        return graQLComponentScanner.delegatesForType(GraQLDelegatingFetch::class.java)
    }

    val batchLoaders:List<GraQLBatchLoaderFactory<Any, Any>> get() {
        return graQLComponentScanner.delegatesForType(GraQLBatchLoaderFactory::class.java)
    }

    val mappedBatchLoaders:List<GraQLMappedBatchLoaderFactory<Any, Any>> get() {
        return graQLComponentScanner.delegatesForType(GraQLMappedBatchLoaderFactory::class.java)
    }
}