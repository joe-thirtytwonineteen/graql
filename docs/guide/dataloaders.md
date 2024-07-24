---
outline: deep
prev:
  text: 'Mutations'
  link: './mutations'
next:
  text: 'Fetch'
  link: './fetch'

---

> [!IMPORTANT]
> GraQL is prerelease, experimental, and a demonstration piece. Use at your own risk.

# Data Loaders (N+1)

> [!NOTE]
> Efficient data loaders are essential and GraQL favors starting with loaders and batch
> fetches over raw fetch implementations that can cause N+1 issues.
 
For a schema listing items, where each item has a "child" (e.g., Books have-one Author),
it's very easy to write code that will list all books and them perform `N` additional queries
(one for each book) to "fetch" the author.

GraQL tackles this up-front, allowing you to designate any method within a `@GraQLComponent`
as a "batch fetch." The underlying `graphql-java` library recognizes this pattern and 
will call your batch-fetch method once (per batch), providing the keys for all child models to load.

## Example

For the given schema definition where a query for `ToDo` can return the `Author` for each
`ToDo`:

```graphql
type Query {
    toDos: [ToDo!]!
}


type ToDo @key(fields: "id") { # <4>
    id: ID!
    title: String!
    completed: Boolean!
    dateCompleted: DateTime
    author: Author!
}

type Author { # <5>
    id: ID!
    username: String!
}
```


### Using a List

> [!NOTE]
> Use a `List` when the child entity is not nullable and you are sure one will exist for every parent.

This method will provide a batch fetch that will list the `Author` for each `ToDo` as a list.

```kotlin
@GraQLBatchFetch
fun author( toDos:Collection<ToDo> ): List<Author> {
    // Find all of our authors, at once, by ID
    return toDoService.findAuthorsByIdIn( toDos.map{it.authorId} )
}
```

### Using a Map

> [!NOTE]
> Use a `Map` when the child entity is not null and may not exist for every parent.

This method will provide a batch fetch that will list the `Author` for each `ToDo`, mapping
each `Author` to its "parent" `ToDo`:

```kotlin
@GraQLBatchFetch
fun author( toDos:Collection<ToDo> ): Map<ToDo, Author> {
    // Find all of our authors, at once, by ID
    val authorsById = toDoService
        .findAuthorsByIdIn( toDos.map{it.authorId} )
        .associateBy{ it.id }

    // Associate them with their ToDos
    return toDos.fold( mutableMapOf() ) { acc, it ->
        acc.put( it, authorsById[it.authorId]!! )
        acc
    }
}
```


## Conventions

GraQL assumes the following sensible defaults:

1. Your method name (`author`) is the same as the field to fetch.
2. The response type from your method (`List` or `Map`) determines if this is a batch fetch or mapped batch fetch.
3. The underlying data loader name will be the concatenation of your method name and "DataLoader"
   (e.g. "authorDataLoader").
4. The GraphQL `type` is the same name as the type generic for the collection passed to the method.


## Customization

These defaults can be overridden within the `GraQLBatchFetch` annotation:

```kotlin
@GraQLBatchFetch(name="authors", type="ToDo", dataLoaderName="customAuthorsDataLoader" )
fun customAuthors( toDos:Collection<CustomToDo> ): List<Author> {
    // Find all of our authors, at once, by ID
    return toDoService.findAuthorsByIdIn( toDos.map{it.authorId} )
}
```
