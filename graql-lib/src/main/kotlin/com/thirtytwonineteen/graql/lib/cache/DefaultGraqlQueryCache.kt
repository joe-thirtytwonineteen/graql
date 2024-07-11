package com.thirtytwonineteen.graql.lib.cache

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.thirtytwonineteen.graql.lib.config.GraQLQueryCacheConfigurationProperties
import graphql.ExecutionInput
import graphql.execution.preparsed.PreparsedDocumentEntry
import graphql.execution.preparsed.PreparsedDocumentProvider
import jakarta.inject.Singleton
import java.util.concurrent.CompletableFuture
import java.util.function.Function

@Singleton
class DefaultGraqlQueryCache( cfg: GraQLQueryCacheConfigurationProperties ) : PreparsedDocumentProvider {

    val cache: Cache<String, PreparsedDocumentEntry>

    init {
        cache = Caffeine.newBuilder()
            .maximumSize( cfg.maxSize )
            .expireAfterAccess( cfg.expireAfterAccess )
            .build()
    }

    override fun getDocumentAsync(
        executionInput: ExecutionInput,
        parseAndValidateFunction: Function<ExecutionInput, PreparsedDocumentEntry>
    ): CompletableFuture<PreparsedDocumentEntry> {

        return CompletableFuture.completedFuture(
            cache.get( executionInput.query ) {
                parseAndValidateFunction.apply( executionInput )
            }
        )

    }

}