package com.thirtytwonineteen.graql.lib.exceptions

import io.micronaut.serde.annotation.Serdeable

@Serdeable
/**
 * Intended for use in the 'extensions' block of a GraphqlError. This isn't an official spec: Netflix's DGS defines it,
 * and I think it's a good idea. They've got an extensive set of enums for some of the props. Starting simple here.
 */
data class GraQLTypedError(
    val errorType: String,
    val errorDetail: String? = null,
    val origin: String? = null,
    val debugInfo: String? = null,
    val debugUri: String? = null
)