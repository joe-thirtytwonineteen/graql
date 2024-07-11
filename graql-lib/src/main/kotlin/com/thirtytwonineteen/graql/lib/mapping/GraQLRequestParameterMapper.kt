package com.thirtytwonineteen.graql.lib.mapping

interface GraQLRequestParameterMapper {
    fun map( input:Any, desiredReturnType:Class<*> ):Any
}



