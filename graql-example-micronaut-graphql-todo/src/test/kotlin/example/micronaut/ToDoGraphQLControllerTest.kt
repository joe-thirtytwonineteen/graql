/*
 * Copyright 2017-2024 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.micronaut

import com.google.gson.Gson
import io.kotest.core.spec.style.AnnotationSpec
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@MicronautTest // <1>
internal class ToDoGraphQLControllerTest(@Inject @Client("/") val client: HttpClient):AnnotationSpec() { // <2>

    @Test
    fun testGraphQLController() {
        // when:
        val now = LocalDateTime.now()
        var todos = allTodos

        // then:
        assertTrue(todos.isEmpty())

        // when:
        val id = createToDo("Test GraphQL", "Tim Yates")
        val id2 = createToDo("Simplify GraphQL", "Some Knucklehead")

        // then: (check it's a UUID)
        assertEquals(1, id)
        assertEquals(2, id2)

        // when:
        todos = allTodos

        // then:
        assertEquals(2, todos.size)
        var todo = todos[0]
        assertEquals("Test GraphQL", todo["title"])
        assertNull( todo["dateCompleted"] )
        assertFalse(java.lang.Boolean.parseBoolean(todo["completed"].toString()))
        assertEquals(
            "Tim Yates",
            (todo["author"] as Map<*, *>?)!!["username"]
        )

        // when:
        val completed = markAsCompleted(id)

        // then:
        assertTrue(completed)

        // when:
        todos = allTodos

        // then:
        assertEquals(2, todos.size)
        todo = todos[0]
        assertEquals("Test GraphQL", todo["title"])
        assertTrue(java.lang.Boolean.parseBoolean(todo["completed"].toString()))
        assertEquals(
            "Tim Yates",
            (todo["author"] as Map<*, *>?)!!["username"]
        )

        assertNotNull( todo["dateCompleted"] )
        val dateCompleted = LocalDateTime.parse(todo["dateCompleted"].toString(), DateTimeFormatter.ISO_DATE_TIME)
        assertTrue(dateCompleted >= now)

        // when:
        val res = createToDoRequest( "nope", "nyet" )

        // then:
        assertNull( res.get("id") )
        assertTrue( res.get("errors") is List<*>)
        val errors = res.get("errors") as List<LinkedHashMap<String, Any>>
        assertTrue( errors.size == 1)
        val error = errors[0] as Map<String, Any>
        val ext = error.get("extensions") as Map<String, Any>
        assertTrue(ext.keys.first().startsWith("createToDo.req.toDo"))


        // TODO: add exception handling and check the message!

    }

    private fun fetch(query: String, allowErrors: Boolean = false): HttpResponse<Map<String, Any>> {
        val request: HttpRequest<String> = HttpRequest.POST("/graphql", query)
        val response = client.toBlocking().exchange(
            request, Argument.mapOf(Argument.STRING, Argument.OBJECT_ARGUMENT)
        )

        // We're asserting for OK, but this isn't REST...
        assertEquals(HttpStatus.OK, response.status())
        Assertions.assertNotNull(response.body())

        // ...let's check the actual response.
        val body = response.body() as Map<*, *>
        if ( body.containsKey("errors") && !allowErrors ) {
            throw Exception(body.get("errors").toString())
        }

        return response
    }

    private fun fetch(query:String, key:String, allowErrors: Boolean = false):Map<*,*> {
        val response = fetch( query, allowErrors )

        val body = response.body() as Map<*, *>
        if ( !body.containsKey("data") ) {
            throw RuntimeException("GraphQL response did not contain 'data' item: maybe it had 'errors'?")
        }

        val data = body.get("data") as Map<*, *>

        if ( !data.containsKey( key ) ) {
            throw IllegalArgumentException("Response did not contain key ${key}")
        }
        if ( data[key] == null && !allowErrors ) {
            throw IllegalArgumentException("Response data key ${key} was null")
        }

        var res:Map<*,*>? = null

        if ( data[key] != null ) {
            res = data.get(key) as Map<*, *>
        } else {
            res = body
        }

        return res
    }

    private val allTodos: List<Map<String, Any>>
        get() {
            return query("toDos", "title, completed, dateCompleted, author { id, username }")
        }

    private fun createToDoRequest(title: String, author: String): Map<*,*> {
        val res = mutate(
            "createToDo",
            mapOf(
                "toDo" to mapOf(
                    "title" to title,
                    "author" to author,
                    "dueDate" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                ),
            ),
            listOf("id"),
            true
        )

        return res
    }

    private fun createToDo(title: String, author: String): Long {
        return createToDoRequest(title, author).get("id").toString().toLong()
    }

    private fun markAsCompleted(id: Long): Boolean {
        return mutate("completeToDo", mapOf("id" to id), listOf("completed"))["completed"] as Boolean
    }

    private fun mutate(name:String, request: Map<Any, Any>, outs:List<String>, allowErrors: Boolean = false):Map<*, *> {
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

        val res = fetch( query, name, allowErrors )

        if ( allowErrors && res.containsKey("errors") ) {
            return res
        } else {
            return outs.fold(mutableMapOf<Any, Any?>()) { acc, it ->
                acc.put( it, res.get(it) )
                return acc
            }
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