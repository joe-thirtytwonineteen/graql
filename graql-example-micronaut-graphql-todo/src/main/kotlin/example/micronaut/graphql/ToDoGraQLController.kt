package example.micronaut.graphql

import com.thirtytwonineteen.graql.*
import example.micronaut.domain.Author
import example.micronaut.domain.ToDo
import example.micronaut.services.*
import graphql.schema.DataFetchingEnvironment
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
    */
    fun author(toDo: ToDo): Author {
        return toDoService.findAuthorById(toDo.authorId)
    }

    @GraQLFetch
    fun author(toDo: ToDo, dfe:DataFetchingEnvironment): CompletableFuture<Any> {
        return dfe.getDataLoader<Any, Any>("authorDataLoader")!!.load(toDo)
    }

    @GraQLMappedDataLoader
    fun authorDataLoader( toDos:Collection<ToDo> ): Map<ToDo, Author> {
        val authorsById = toDoService
            .findAuthorsByIdIn( toDos.map{it.authorId} )
            .associateBy{ it.id }

        return toDos.fold( mutableMapOf() ) { acc, it ->
            acc.put( it, authorsById[it.authorId]!! )
            acc
        }
    }

    /*
    @GraQLMappedDataLoader
    fun authorDataLoader( ids:Collection<Long> ): Map<Long, Author> {
        LOG.info("authorDataLoader for $ids")

        return toDoService
            .findAuthorsByIdIn( ids.toList() )
            .associateBy{ it.id!! }
    }
    */

    companion object {
        private val LOG = LoggerFactory.getLogger(ToDoGraQLController::class.java)
    }

}