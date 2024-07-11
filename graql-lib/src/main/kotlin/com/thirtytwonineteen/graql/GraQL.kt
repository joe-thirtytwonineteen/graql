package com.thirtytwonineteen.graql

import com.thirtytwonineteen.graql.lib.config.micronaut.GraQLBeanScanner
import com.thirtytwonineteen.graql.lib.exceptions.GraQLExceptionTranslatorReference
import com.thirtytwonineteen.graql.lib.delegates.*
import graphql.schema.GraphQLScalarType
import jakarta.inject.Singleton

@Singleton
class GraQL(
    private val graQLBeanScanner: GraQLBeanScanner
) {

    val scalars:Set<GraphQLScalarType> get() {
        return graQLBeanScanner.graQLScalars
    }

    val queries:List<GraQLDelegatingQuery<Any>> get() {
        return graQLBeanScanner.delegatesForType(GraQLDelegatingQuery::class.java)
    }

    val mutations:List<GraQLDelegatingMutation<Any>> get() {
        return graQLBeanScanner.delegatesForType(GraQLDelegatingMutation::class.java)
    }

    val fetches:List<GraQLDelegatingFetch<Any>> get() {
        return graQLBeanScanner.delegatesForType(GraQLDelegatingFetch::class.java)
    }

    val batchLoaders:List<GraQLDelegatingBatchLoader<Any, Any>> get() {
        return graQLBeanScanner.delegatesForType(GraQLDelegatingBatchLoader::class.java)
    }

    val mappedBatchLoaders:List<GraQLDelegatingMappedBatchLoader<Any, Any>> get() {
        return graQLBeanScanner.delegatesForType(GraQLDelegatingMappedBatchLoader::class.java)
    }
}