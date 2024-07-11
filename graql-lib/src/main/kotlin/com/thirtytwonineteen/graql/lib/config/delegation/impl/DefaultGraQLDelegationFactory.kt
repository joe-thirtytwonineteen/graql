package com.thirtytwonineteen.graql.lib.config.delegation.impl

import com.thirtytwonineteen.graql.*
import com.thirtytwonineteen.graql.lib.config.delegation.GraQLDelegationConfigurator
import com.thirtytwonineteen.graql.lib.config.delegation.GraQLDelegationFactory
import jakarta.inject.Named
import jakarta.inject.Singleton

@Singleton
@Named("graQLDelegationFactory")
open class DefaultGraQLDelegationFactory(
    @Named("graQLFetchConfigurator") private val fetchConfigurator: GraQLDelegationConfigurator<GraQLFetch>,
    @Named("graQLQueryConfigurator") private val queryConfigurator: GraQLDelegationConfigurator<GraQLQuery>,
    @Named("graQLMutationConfigurator") private val mutationConfigurator: GraQLDelegationConfigurator<GraQLMutation>,
    @Named("graQLDataLoaderConfigurator") private val dataLoaderConfigurator: GraQLDelegationConfigurator<GraQLDataLoader>,
    @Named("graQLMappedDataLoaderConfigurator") private val mappedDataLoaderConfigurator: GraQLDelegationConfigurator<GraQLMappedDataLoader>,
    @Named("graQLMappedBatchFetchConfigurator") private val mappedBatchFetchConfigurator: GraQLDelegationConfigurator<GraQLMappedBatchFetch>,
) : GraQLDelegationFactory {

    override val delegateConfigurators = mapOf(
        GraQLFetch::class to fetchConfigurator,
        GraQLQuery::class to queryConfigurator,
        GraQLMutation::class to mutationConfigurator,
        GraQLDataLoader::class to dataLoaderConfigurator,
        GraQLMappedDataLoader::class to mappedDataLoaderConfigurator,
        GraQLMappedBatchFetch::class to mappedBatchFetchConfigurator,
    )

}