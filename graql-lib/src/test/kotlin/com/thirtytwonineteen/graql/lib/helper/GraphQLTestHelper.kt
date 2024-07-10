package com.thirtytwonineteen.graql.config.micronaut.helper

import com.google.gson.Gson
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals

@Singleton
class GraphQLTestHelper(
    @Inject @Client("/") val client: HttpClient
) {

    private fun fetch(query: String): HttpResponse<Map<String, Any>> {
        val request: HttpRequest<String> = HttpRequest.POST("/graphql", query)
        val response = client.toBlocking().exchange(
            request, Argument.mapOf(Argument.STRING, Argument.OBJECT_ARGUMENT)
        )

        // We're asserting for OK, but this isn't REST...
        assertEquals(HttpStatus.OK, response.status())
        Assertions.assertNotNull(response.body())

        // ...let's check the actual response.
        val body = response.body() as Map<*, *>
        if ( body.containsKey("errors") ) {
            throw Exception(body.get("errors").toString())
        }

        return response
    }

    private fun fetch(query:String, key:String):Map<*,*> {
        val response = fetch( query )

        val body = response.body() as Map<*, *>
        if ( !body.containsKey("data") ) {
            throw RuntimeException("GraphQL response did not contain 'data' item: maybe it had 'errors'?")
        }

        val data = body.get("data") as Map<*, *>

        if ( !data.containsKey( key ) ) {
            throw IllegalArgumentException("Response did not contain key ${key}")
        }
        if ( data[key] == null ) {
            throw IllegalArgumentException("Response data key ${key} was null")
        }
        return data[key] as Map<*,*>

    }
    private fun mutate(name:String, request: Map<Any, Any>, outs:List<String>):Map<*, *> {
        val query = toQuery("""
            mutation { 
                $name(
                    input: { ${mapToQuery( request )} }                
                ) 
                { 
                    ${outs.joinToString(",") } 
                }
            }
        """)

        val res = fetch( query, name )

        return outs.fold(mutableMapOf<Any, Any?>()) { acc, it ->
            acc.put( it, res.get(it) )
            return acc
        }
    }

    private fun query(name:String, impl:String):List<Map<String, Any>> {
        val query = toQuery("query { $name { ${impl} } }")
        val response = fetch(query)
        val body = response.body() as Map<*, *>
        if ( !body.containsKey("data") ) {
            throw RuntimeException("GraphQL response did not contain 'data' item: maybe it had 'errors'?")
        }

        val data = body.get("data") as Map<*, *>

        return data[name] as List<Map<String, Any>>
    }

    private fun toQuery( graphQlString: String):String {
        return Gson().toJson(
            Query( graphQlString.trimIndent().replace("\n", "") )
        )
    }

    private fun mapToQuery(inMap:Map<Any, Any> ):String {
        val mapFolder = { inMap:Map<*,*> ->
            val inner = inMap.keys.fold("") { acc, it ->
                acc + "${it}: ${Gson().toJson(inMap[it])},"
            }
            "{$inner}"
        }

        return inMap.keys.fold("") { acc, it ->
            val c = inMap[it]
            val v = when {
                (c is Map<*,*>) -> mapFolder( c )
                else -> Gson().toJson(inMap[it])
            }
            acc + "${it}: ${v},"
        }
    }


    private class Query(val query:String)

}