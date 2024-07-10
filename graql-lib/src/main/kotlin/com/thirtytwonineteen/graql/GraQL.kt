package com.thirtytwonineteen.graql

import com.thirtytwonineteen.graql.lib.config.micronaut.GraQLBeanScanner
import com.thirtytwonineteen.graql.lib.fetcher.*
import com.thirtytwonineteen.graql.lib.loader.GraQLDelegatingMappedBatchLoader
import jakarta.inject.Singleton

@Singleton
class GraQL(
    private val graQLBeanScanner: GraQLBeanScanner
) {

    val queries:List<GraQLDelegatingQuery<Any>> get() {
        return graQLBeanScanner.delegatesForAnnotation(GraQLQuery::class)
    }

    val mutations:List<GraQLDelegatingMutation<Any>> get() {
        return graQLBeanScanner.delegatesForAnnotation(GraQLMutation::class)
    }

    val fetches:List<GraQLDelegatingFetch<Any>> get() {
        return graQLBeanScanner.delegatesForAnnotation(GraQLFetch::class)
    }

    val mappedBatchLoaders:List<GraQLDelegatingMappedBatchLoader<Any,Any>> get() {
        return graQLBeanScanner.delegatesForAnnotation(GraQLMappedDataLoader::class)
    }

}