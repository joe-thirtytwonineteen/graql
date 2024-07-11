# graQL _(gra · kl)_ 

A stab at near-zero-boilerplate GraphQL configuration for [micronaut®](https://micronaut.io/)'s existing GraphQL
integration.

# Purpose

Take a stab at bringing the easy configuration of Spring GraphQL and Netflix DGS to Micronaut. 

# tl;dr 

Create a schema, add `@GraQLComponent` to a controller, add `@GraQLQuery` to methods that get data, `@GraQLMutation`
to methods that change data, and off you go. We're trying to use sensible defaults: no other config needed. 

# Status

GraQL is about one step beyond an experiment. There's a reason you'd need to clone this repo to use it.

## Working*

| Feature                       | Annotation                                                               | Spring Equivalent          | DGS Equivalent                |
|-------------------------------|--------------------------------------------------------------------------|----------------------------|-------------------------------|
| Queries                       | `@GraQLQuery`                                                            | `@QueryMapping`            | `@DgsQuery`                   |
| Mutations                     | `@GraQLMutation`                                                         | `@MutationMapping`         | `@DgsMutation`                |
| Data Loaders                  | `@GraQLDataLoader`, `@GraQLMappedDataLoader`                             | Sort of manual             | `@DgsDataLoader`              |
| Data Loaders for Fetch (N+1)  | `@GraQLBatchFetch`                                                       | `@BatchMapping`            | Sort of manual                |
| Federation                    | Automatic!                                                               | Simple to Configure        | Automatic!                    |
| Validation                    | Micronaut Validation!                                                    | Spring                     | Spring                        |
| Security (cross-thread!)      | Micronaut Propagration!                                                  | Read the docs | Read the docs                 | 
| Extensible Exception Handling | `@GraQLExceptionHandler` (and logs the real exception!)                  | `@GraphQlExceptionHandler` | Roll your own (but it's easy) |
| Query Caching                 | Automatic, configuration in application.properties, replaceable at will. | Do it yourself.            | Just add their bean           |          |
| Query DSL/CodeGen | Netflix DGS Codegen-compatible                                           | DSL | DGS CodeGen                   |

* _well enough for a not-yet-in-production application_


## To-Do

1. Pagination (emerging-standards-based!)
2. Subscriptions

# Philosophy

1. POJOs whenever possible
2. Make sure you/we can replace internals through named bean replacements
3. Stay extensible: the code delegation-via-annotation supports overriding to create new annotations at will

# Five-Minute Documentation

Given a normal-old `Controller` with single-or-no argument methods that'd delegate to a service, mark it up with
`@GraQL...` annotations. When GraphQL wants to do its nasty N+1 bits to crawl the graph, use `@GraQLBatchFetch` to
keep your DBA from hunting for you:

```
@GraQLComponent
class BookController( val bookService:BookService ) {

    @GraQLQuery
    fun list(req:ListBooksRequest):ListBooksResponse {
        return bookService.list( req )
    }
    
    // We'll automatically handle validation failures and return something
    // intelligent within errors.extensions.
    @GraQLMutation
    fun create(@field:Valid req:CreateBookRequest):CreateBookResponse {
        return bookService.create( req )
    }
    
    // N+1 helper for when people fetch book.author across 50 books! We'll autodetect if this this becomes
    // a list or mapped batch fetch based on your method signature, and conventionally create a named
    // "authorDataLoader" you can use anywhere else.
    @GraQLBatchFetch 
    fun author( books:Collection<Book> ): List<Author> {
        
        return authorsById = bookService.findAuthorsByIdIn( books.map{ it.authorId } )
    }
}
```
That's it. On startup, GraQL will scan for all `GraQLComponent` and do its thing. Because it leverages [Micronaut
GraphQL Integration](https://micronaut-projects.github.io/micronaut-graphql/4.4.0/guide/), all of the existing 
configuration it provides (like `/graphql` and `/graphiql`) is as normal!

# Actual Documentation

That's tomorrow. We'd told some folks we'd start to get things out this week.

# License

See LICENSE

# Legal Bits

1. ./graql-lib is copyright 2024, thirtytwonineteen, llc
2. "[micronaut®](https://micronaut.io/)" is a trademark of Object Computing, Inc. and I hope I read the brand guidelines correctly for using its
name.
2. "GraphQL" is published by the [The GraphQL Foundation](https://graphql.org/)

