package com.thirtytwonineteen.graql.lib.config.properties

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties(value = "graql")
class GraQLConfigurationProperties(
    val queryCache: GraQLQueryCacheConfigurationProperties
) {
    var schemaLocations = listOf("classpath:schema.graphqls")
}