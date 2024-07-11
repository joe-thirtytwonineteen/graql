package example.micronaut.services

import example.micronaut.domain.Author
import example.micronaut.domain.ToDo
import example.micronaut.persistence.AuthorRepository
import example.micronaut.persistence.ToDoRepository
import io.micronaut.core.annotation.Introspected
import jakarta.inject.Inject
import jakarta.inject.Singleton
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Singleton
open class ToDoService(
    @Inject private val toDoRepository: ToDoRepository,
    @Inject private val authorRepository: AuthorRepository,
) {

    open fun createToDo(@Valid req: CreateToDoRequest): CreateToDoResponse {
        // Authorization
        // None

        // Operation
        val dto = req.toDo
        val author = authorRepository.findOrCreate(dto.author)
        val toDo = toDoRepository.save(
            ToDo(dto.title, author.id!!)
        )
        return CreateToDoResponse(
            toDo.id!!,
            authorId = author.id!!
        )
    }

    fun findAllToDos(req: FindToDosRequest):Collection<ToDo> {
        // Authorization
        // None

        // Operation
        return toDoRepository.findAllOrderByIdAsc()
    }

    fun findToDoById(id: Long) : ToDo {
        // Authorization
        // None

        // Operation
        return toDoRepository.findById(id).orElseThrow()
    }

    fun completeToDo(req: CompleteToDoRequest): CompleteToDoResponse {
        // Authorization
        // None

        // Operation
        val toDo = findToDoById( req.id )
        toDo.completed = true
        toDo.dateCompleted = LocalDateTime.now()
        toDoRepository.update(toDo)

        return CompleteToDoResponse( req.id )
    }

    fun findAuthorsByIdIn(ids: List<Long>) : List<Author> {
        // Authorization
        // None

        // Operation
        return authorRepository.findByIdIn(ids.toList())
    }

    fun findAuthorById(id:Long) : Author {
        // Authorization
        // None

        // Operation
        return authorRepository.findById( id ).orElseThrow()
    }

}

class FindToDosRequest() {
    // Room to grow! Filters? Pagination (foreshadowing...)? Add them here!
}
data class CompleteToDoRequest( val id: Long )
data class CompleteToDoResponse( val id: Long, val completed:Boolean = true )
@Introspected
data class CreateToDoRequest(@Valid val toDo: ToDoDTO)

@Introspected
data class ToDoDTO(
    @field:Size(min = 5) val title: String,
    val author:String
)

data class CreateToDoResponse( val id: Long, val authorId: Long )
