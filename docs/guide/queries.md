---
outline: deep
prev:
  text: 'Getting Started'
  link: './gettingstarted'
next:
  text: 'Mutations'
  link: './mutations'

---

> [!IMPORTANT]
> GraQL is prerelease, experimental, and a demonstration piece. Use at your own risk.

# Queries

GraQL allows you to designate any method within a `@GraQLComponent` as a GraphQL query.

## Example

For the given schema definition:

```graphql
type Query {
    toDos: [ToDo!]!
}
```

This method will provide the query:

```kotlin
@GraQLQuery
fun toDos(): Collection<ToDo> {
    // ...implementation
}
```

## Conventions

GraQL assumes the following sensible defaults:

1. Your method name (`toDos`) is the same as your query name
2. If your method requires input, it will be a single argument named the same as your method parameter.


## Customization

These defaults can be overridden within the `GraQLQuery` annotation:

```kotlin
@GraQLQuery(name = "toDos", input = "arg")
fun customNamedMethod( arg: ToDoRequest ): Collection<ToDo> {
    // ...implementation
}


```
