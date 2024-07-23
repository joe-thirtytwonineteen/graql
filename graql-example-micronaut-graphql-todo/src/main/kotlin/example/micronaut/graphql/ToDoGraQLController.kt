package example.micronaut.graphql

import com.thirtytwonineteen.graql.*
import example.micronaut.domain.Author
import example.micronaut.domain.ToDo
import example.micronaut.filter.ExamplePropagationContext
import example.micronaut.filter.ExampleRequestFilter
import example.micronaut.filter.RequestInfo
import example.micronaut.services.*
// import graphql.schema.DataFetchingEnvironment
import io.micronaut.core.propagation.PropagatedContext
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.logging.Logger

@GraQLComponent
class ToDoGraQLController(
    private val graQL: GraQL,
    private val toDoService: ToDoService,
) {

    @GraQLQuery
    fun toDos(request: FindToDosRequest?): Collection<ToDo> {
        return toDoService.findAllToDos(request ?: FindToDosRequest())
    }

    @GraQLMutation
    fun createToDo(req: CreateToDoRequest): CreateToDoResponse {
        return toDoService.createToDo(req)
    }

    @GraQLMutation
    fun completeToDo(req: CompleteToDoRequest): CompleteToDoResponse {
        return toDoService.completeToDo(req)
    }

    /* Naive
    @GraQLFetch
    fun author(toDo: ToDo): Author {
        return toDoService.findAuthorById(toDo.authorId)
    }
    */

    /* Better, but two methods and dealing with DataFetchingEnvironment
    @GraQLFetch
    fun author(toDo: ToDo, dfe:DataFetchingEnvironment): CompletableFuture<Any> {
        return dfe.getDataLoader<Any, Any>("authorDataLoader")!!.load(toDo)
    }

    @GraQLMappedDataLoader
    fun authorDataLoader( toDos:Collection<ToDo> ): Map<ToDo, Author> {
        // Find all of our authors, at once, by ID
        val authorsById = toDoService
            .findAuthorsByIdIn( toDos.map{it.authorId} )
            .associateBy{ it.id }

        // Associate them with their ToDos
        val authors:Map<ToDo, Author> = toDos.fold( mutableMapOf() ) { acc, it ->
            acc.put( it, authorsById[it.authorId]!! )
            acc
        }

        /*
        This has NOTHING to do with dataloaders: it's here to show that Micronaut propogated contexts
        are, in fact, working. We're on a separate thread, but we should be on the same request number.

        In other words, you're going across threads, but your security/authn should still work.
        */
        val requestId = ExampleRequestFilter.requests
        val httpRequestInfo = PropagatedContext.get().get( ExamplePropagationContext::class.java ).updateThreadContext()
        if ( httpRequestInfo != null ) {
            assert( requestId == httpRequestInfo.requestCount )
            assert( Thread.currentThread().id != httpRequestInfo.threadId )
        }

        return authors
    }
    */

    /* Good, like Spring BatchMapping with MappedBatchLoader */
    @GraQLBatchFetch
    fun author( toDos:Collection<ToDo> ): Map<ToDo, Author> {
        // Find all of our authors, at once, by ID
        val authorsById = toDoService
            .findAuthorsByIdIn( toDos.map{it.authorId} )
            .associateBy{ it.id }

        // Associate them with their ToDos
        val authors:Map<ToDo, Author> = toDos.fold( mutableMapOf() ) { acc, it ->
            acc.put( it, authorsById[it.authorId]!! )
            acc
        }

        /*
        This has NOTHING to do with dataloaders: it's here to show that Micronaut propogated contexts
        are, in fact, working. We're on a separate thread, but we should be on the same request number.

        In other words, you're going across threads, but your security/authn should still work.
        */
        val requestId = ExampleRequestFilter.requests
        val httpRequestInfo = PropagatedContext.get().get( ExamplePropagationContext::class.java ).updateThreadContext()
        if ( httpRequestInfo != null ) {
            assert( requestId == httpRequestInfo.requestCount )
            assert( Thread.currentThread().id != httpRequestInfo.threadId )
        }

        /* Enough about that: return our authors */
        return authors
    }

    /*
    Also good, like Spring BatchMapping with BatchLoader (List, not Map),
    but this will fail if toDo.author is nullable and any are missing!
    @GraQLBatchFetch
    fun author( toDos:Collection<ToDo> ): List<Author> {
        // Find all of our authors, at once, by ID
        return toDoService.findAuthorsByIdIn( toDos.map{it.authorId} )
    }
    */

}