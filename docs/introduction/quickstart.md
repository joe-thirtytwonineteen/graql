---
outline: deep
next:
    text: 'About GraQL'
    link: './about'
---

> [!IMPORTANT]
> GraQL is prerelease, experimental, and a demonstration piece. Use at your own risk.
 
# Quickstart

This page is designed to help you quickly make the leap from [micronaut®](https://micronaut.io/)'s basic GraphQL
integration to a low-configuration, team-friendly, sensible-default workflow for publishing
GraphQL APIs.

## The Problem: GraphQLFactory

Leaping straight from the stock [micronaut®](https://micronaut.io/)'s GraphQL dependency to writing code
leaves your team maintaining a GraphQL "factory" bean. 

All contributors will need to register every query, mutation, and fetch with a growing pile of boilerplate:

```kotlin
RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
    .type("Query", typeWiring -> typeWiring  
            .dataFetcher("books", bookDataFetcher))

    .type("Mutation", typeWiring -> typeWiring 
            .dataFetcher("createBook", createBookDataFetcher)
            .dataFetcher("readBook", readBookDoDataFetcher))

    .type("Book", typeWiring -> typeWiring 
            .dataFetcher("author", authorDataFetcher))
    .type("Magazine", typeWiring -> typeWiring 
            .dataFetcher("author", authorDataFetcher))

    .build();
```

## GraQL's Solution

GraQL's solution isn't groundbreaking: annotate your existing code, similar to Netflix DGS and Spring
GraphQL.

With good naming practices and single-argument methods, there's barely anything to do:

```kotlin

/*
Annotate any existing controller or create a GraphQL-specific component with 
@GraQLComponent to mark it singleton providing GraphQL data fetchers.  
*/
@GraQLComponent
class BookController(
    private val bookService: BookService,
    private val authorService: BookService,
) {

    /*
    Register a Query with @GraQL, defaulting to the method name as 
    the name of the GraphQL dataFetcher.
    */
    @GraQLQuery
    fun books(request: FindBooksRequest?): Collection<Book> {
        /* Any typical format-specific controller-tier stuff here! */
        /* ...stuff... */
        
        /* Ok, delegate to a service */
        return bookService.findBooks(request ?: FindToDosRequest())
    }
    
    /*
    Register a Mutation with @GraQL, defaulting to the method name as 
    the name of the GraphQL dataFetcher.
    */
    @GraQLMutation
    fun createBook(request: CreateBookRequest): CreateBookResponse {
        return bookService.createBook( request )
    }

    /*
    GraQL encourages *immediate addressing of the N+1 problem* by favoring
    batch fetches! When books are listed and the consumer requests their
    authors, GraQL will batch-fetch authors instead of seeking them for
    each Book in the result.
     */
    @GraQLBatchFetch
    fun author( books:Collection<Book> ): List<Book> {
        // Find all of our authors, at once, by ID
        return authorService.findAuthorsByIdIn( books.map{it.authorId} )
    }

}
```

