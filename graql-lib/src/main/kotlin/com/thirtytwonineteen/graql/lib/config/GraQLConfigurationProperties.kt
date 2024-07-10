package com.thirtytwonineteen.graql.lib.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties(value = "graql")
class GraQLConfigurationProperties {
    var schemaLocations = listOf("classpath:schema.graphqls")
}

