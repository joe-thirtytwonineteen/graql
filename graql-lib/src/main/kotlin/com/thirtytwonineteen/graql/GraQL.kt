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
        return graQLBeanScanner.delegatesForAnnotation(GraQLDelegatingQuery::class.java)
    }

    val mutations:List<GraQLDelegatingMutation<Any>> get() {
        return graQLBeanScanner.delegatesForAnnotation(GraQLDelegatingMutation::class.java)
    }

    val fetches:List<GraQLDelegatingFetch<Any>> get() {
        return graQLBeanScanner.delegatesForAnnotation(GraQLDelegatingFetch::class.java)
    }

    val mappedBatchLoaders:List<GraQLDelegatingMappedBatchLoader<Any,Any>> get() {
        return graQLBeanScanner.delegatesForAnnotation(GraQLDelegatingMappedBatchLoader::class.java)
    }

}