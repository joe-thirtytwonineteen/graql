package com.thirtytwonineteen.graql.lib.federation

abstract class GraQLFederatedEntityResolver<T>(
    val entityName:String,
    val schemaObjectName:String,
    val entityType: Class<T>,
    val resolution: ( params:Map<String, Any> ) -> T
) {
    constructor(entityType: Class<T>, resolution:( params:Map<String, Any> ) -> T) : this(
        entityType.simpleName, entityType.simpleName, entityType, resolution
    )
}
