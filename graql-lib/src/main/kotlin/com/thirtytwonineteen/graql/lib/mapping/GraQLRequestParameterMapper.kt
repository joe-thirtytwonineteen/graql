package com.thirtytwonineteen.graql.lib.mapping

import com.google.gson.Gson
import jakarta.inject.Named
import jakarta.inject.Singleton

interface GraQLRequestParameterMapper {
    fun map( input:Any, desiredReturnType:Class<*> ):Any
}

@Singleton @Named("graQLRequestParameterMapper")
class DefaultGraQLRequestParameterMapper : GraQLRequestParameterMapper {
    override fun map( input: Any, desiredReturnType:Class<*> ):Any {
        return Gson().fromJson(Gson().toJson(input), desiredReturnType)
    }
}



