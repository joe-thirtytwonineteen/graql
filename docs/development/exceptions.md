---
outline: deep
prev:
  text: 'Query Caching'
  link: '../guide/caching'
next:
  text: 'Input Validation'
  link: './validation'
---

> [!IMPORTANT]
> GraQL is prerelease, experimental, and a demonstration piece. Use at your own risk.

# Exception Handling

GraphQL is designed to mask underlying exceptions by default. This makes sense, and it's good
security practice: there's no sense sending stack traces back to consumers!

However, the messages sent back can be less than helpful. 

To help, GraQL allows you to designate any method within a `@GraQLComponent` as an exception handler
for a given type of `Exception`.

## Example

To handle `MyCustomException`, a subclass of `Exception`:

```kotlin
class MyCustomException:RuntimeException("Custom exception!")
```

This method will be called, allowing you to build a better exception message and GraphQL 
extensions:

```kotlin
@GraQLExceptionHandler
fun handleValidationException(ex: MyCustomException, builder: GraphqlErrorBuilder<*>) {
    builder
        .message("Whoa nelly! That didn't go as planned!")
        .errorType( ErrorType.DataFetchingException )
}

```

## Further Reading

The [Netflix DGS team has published guidelines for how they handle exceptions](https://netflix.github.io/dgs/error-handling/).

We think they're a good idea, and GraQL's [validation integration](./validation) uses their guidelines.

