package com.thirtytwonineteen.graql

import com.thirtytwonineteen.graql.lib.config.micronaut.GraQLBeanScanner
import com.thirtytwonineteen.graql.lib.fetcher.*
import com.thirtytwonineteen.graql.lib.loader.GraQLDelegatingMappedBatchLoader
import jakarta.inject.Singleton

@Singleton
class GraQL(
    private val graQLBeanScanner: GraQLBeanScanner
) {

    val queries:List<GraQLDelegatingQuery> get() {
        return graQLBeanScanner.delegatesForType(GraQLDelegatingQuery::class)
    }

    val mutations:List<GraQLDelegatingMutation> get() {
        return graQLBeanScanner.delegatesForType(GraQLDelegatingMutation::class)
    }

    val fetches:List<GraQLDelegatingFetch> get() {
        return graQLBeanScanner.delegatesForType(GraQLDelegatingFetch::class)
    }

    val mappedBatchLoaders:List<GraQLDelegatingMappedBatchLoader> get() {
        return graQLBeanScanner.delegatesForType(GraQLDelegatingMappedBatchLoader::class)
    }

}