package com.thirtytwonineteen.graql.lib.fixture

import com.thirtytwonineteen.graql.GraQLComponent
import com.thirtytwonineteen.graql.GraQLMappedDataLoader
import com.thirtytwonineteen.graql.GraQLMutation
import com.thirtytwonineteen.graql.GraQLQuery
import jakarta.inject.Singleton

@GraQLComponent
class GraQLFixtureComponent {

    @GraQLQuery
    fun isAQuery( req: GraQLFixtureRequestType ): GraQLFixtureResponseType {
        return GraQLFixtureResponseType( req.ding.uppercase() )
    }
    @GraQLQuery(name="isANamedQuery")
    fun isAQueryWithAName( req: GraQLFixtureRequestType ): GraQLFixtureResponseType {
        return GraQLFixtureResponseType( req.ding.uppercase() )
    }
    @GraQLQuery(name="isANamedQueryWithInputName", input="wackyKeyName")
    fun isAQueryWithANameAndInput( req: GraQLFixtureRequestType ): GraQLFixtureResponseType {
        return GraQLFixtureResponseType( req.ding.uppercase() )
    }

    @GraQLMutation
    fun isAMutation( whatever: GraQLFixtureRequestType ): GraQLFixtureResponseType {
        return GraQLFixtureResponseType( whatever.ding.uppercase() )
    }
    @GraQLMutation(name="isANamedMutation")
    fun isAMutationWithAName( input: GraQLFixtureRequestType ): GraQLFixtureResponseType {
        return GraQLFixtureResponseType( input.ding.uppercase() )
    }
    @GraQLMutation(name="isANamedMutationWithInputName", input = "wackyKeyName")
    fun isAMutationWithANameAndInput( moo:GraQLFixtureRequestType ): GraQLFixtureResponseType {
        return GraQLFixtureResponseType( moo.ding.uppercase() )
    }

    @GraQLMappedDataLoader
    @GraQLMappedDataLoader(name="yetMoreThings")
    fun things( keys: Set<Long> ):Map<*, *> {
        return keys.toList()
            .map{ mapOf("id" to it, "name" to "Thing ${it}") }
            .associateBy { it.get("id") }
    }

    @GraQLMappedDataLoader(name="otherThings")
    fun thingsNamedFunny( keys: Set<Long> ):Map<*, *> {
        return keys.toList()
            .map{ mapOf("id" to it, "name" to "Thing ${it}") }
            .associateBy { it.get("id") }
    }

    fun isNotAQuery() {}
}

@Singleton
class NotAGraQLFixtureComponent

class GraQLFixtureResponseType(val dong:String)
class GraQLFixtureRequestType(val ding:String)
