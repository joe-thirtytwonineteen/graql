package example.micronaut.graphql

import com.thirtytwonineteen.graql.*
import example.micronaut.domain.Author
import example.micronaut.domain.ToDo
import example.micronaut.services.*

@GraQLComponent
class ToDoGraQLController(
    private val toDoService: ToDoService
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

    @GraQLMappedDataLoader
    fun author( ids:Collection<Long> ): Map<Long, Author> {
        return toDoService
            .findAuthorsByIdIn( ids.toList() )
            .associateBy{ it.id!! }
    }

    @GraQLFetch(field="author")
    fun findAuthorByToDo( toDo: ToDo): Author {
        return toDoService.findAuthorById(toDo.authorId)
    }

}