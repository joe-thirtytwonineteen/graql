package example.micronaut

import com.thirtytwonineteen.graql.*

@GraQLComponent
class GraQLController(private val dbRepository: DbRepository) { // <1>

    @GraQLQuery
    fun bookById(id:String):Book? {
        return dbRepository.findAllBooks()
            .firstOrNull { book: Book -> (book.id == id) }
    }

    @GraQLFetch(field="author")
    fun findAuthorByToDo( book:Book? ):Author? {
        when {
            book == null -> return null
            else -> {
                return dbRepository.findAllAuthors() // <7>
                    .firstOrNull { author: Author -> (author.id == book.author.id) }
            }
        }
    }

}
