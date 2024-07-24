---
outline: deep
prev:
  text: 'Queries'
  link: './queries'
next:
  text: 'Data Loaders'
  link: './dataloaders'

---

> [!IMPORTANT]
> GraQL is prerelease, experimental, and a demonstration piece. Use at your own risk.

# Mutations

GraQL allows you to designate any method within a `@GraQLComponent` as a GraphQL mutation.

## Example

For the given schema definition:

```graphql
type Mutation {
    createToDo(input: CreateToDoRequest!): CreateToDoResponse  # <2>
}
```

This method will provide the mutation:

```kotlin
@GraQLMutation
fun createToDo(input: CreateToDoRequest): CreateToDoResponse {
    return toDoService.createToDo(req)
}
```

## Conventions

GraQL assumes the following sensible defaults:

1. Your method name (`createToDo`) is the same as your mutation name
2. Your method should require a single input and its name (within your schema) is `input`


## Customization

These defaults can be overridden within the `GraQLMutation` annotation:

```kotlin
@GraQLMutation(name = "createToDo", input = "notInput")
fun customNamedCreationMethod( input: CreateToDoRequest ): CreateToDoResponse {
    // ...implementation
}


```
