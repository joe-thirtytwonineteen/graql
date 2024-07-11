package com.thirtytwonineteen.graql.lib.config

import io.micronaut.context.annotation.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(value = "graql")
class GraQLConfigurationProperties(
    val queryCache:GraQLQueryCacheConfigurationProperties
) {
    var schemaLocations = listOf("classpath:schema.graphqls")
}

@ConfigurationProperties(value = "graql.queryCache")
class GraQLQueryCacheConfigurationProperties {

    var maxSize:Long = 100
    var expireAfterAccess: Duration = Duration.ofHours(1)

}

