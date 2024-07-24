---
outline: deep
prev:
  text: 'Data Loaders (N+1)'
  link: './dataloaders'
next:
  text: 'Scalars'
  link: './scalars'

---

> [!IMPORTANT]
> GraQL is prerelease, experimental, and a demonstration piece. Use at your own risk.

# Fetch

> [!NOTE]
> Be careful with fetch!
> 
> Efficient data loaders are essential and GraQL favors starting with loaders and batch
> fetches over raw fetch implementations that can cause N+1 issues. 

GraQL allows you to designate any method within a `@GraQLComponent` as a GraphQL fetch.

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

This method will provide a fetch that will find the `Author` for a given`ToDo`:

```kotlin
@GraQLFetch
fun author(toDo: ToDo): Author {
    return toDoService.findAuthorById(toDo.authorId)
}
```

## Conventions

GraQL assumes the following sensible defaults:

1. Your method name (`author`) is the same as the field to fetch.
4. The GraphQL `type` is the same name as the type of the single argument passed to the method.


## Customization

These defaults can be overridden within the `GraQLFetch` annotation:

```kotlin
@GraQLFetch(name="author", type="ToDo")
fun customAuthor(toDo: CustomToDo): Author {
   return toDoService.findAuthorById(toDo.authorId)
}
```
