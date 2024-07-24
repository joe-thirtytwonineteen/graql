---
outline: deep
prev:
  text: 'Input Validation'
  link: './validation'
next:
  text: 'Startup Scannning'
  link: '../internals/scanning'
---

> [!IMPORTANT]
> GraQL is prerelease, experimental, and a demonstration piece. Use at your own risk.

# Security and Threads

In line with Netflix DGS and Spring GraphQL, GraQL does *not* provide a security framework.

Authentication (identity) and authorization (can an identity perform an operation) should be handled
within your endpoint (`/graphql`) and business (service) tier, respectively.

In simpler terms, you secure the `/graphql` route as you would any other, and your `@GraQLComponents`
should delegate to services that perform authorization.

Where GraQL *does* provide a convenience, it's within *the cross-thread nature of batched data fetching/data loading*,
and *GraQL does not reinvent wheels*. Instead, you can rely on the existing mechanics of Micronaut
context propagation.

## Data Loader and Threads

Within the following batched data loader, multiple batches of authors may be fetched. This is asynchronous
and *may cross threads*. 

This means that any authorization (i.e. "current user") bound to a thread might be lost.

```kotlin
@GraQLBatchFetch
fun author( toDos:Collection<ToDo> ): List<Author> {
    // Find all of our authors, at once, by ID
    return toDoService.findAuthorsByIdIn( toDos.map{it.authorId} )
}
```

## Solving With Context Propagation

Micronaut provides [context propagation](https://docs.micronaut.io/latest/guide/#contextPropagation) to solve
this, and GraQL does not change anything about its implementation. Any existing usage "just works."


