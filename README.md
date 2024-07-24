# graQL _(gra · kl)_ 

A stab at near-zero-boilerplate GraphQL configuration for [micronaut®](https://micronaut.io/)'s existing GraphQL
integration.

# Purpose

Take a stab at bringing the easy configuration of Spring GraphQL and Netflix DGS to Micronaut. 

# Documentation

Available via [GitHub Pages](https://joe-thirtytwonineteen.github.io/graql/).

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


# License

See LICENSE

# Legal Bits

1. ./graql-lib is copyright 2024, thirtytwonineteen, llc
2. "[micronaut®](https://micronaut.io/)" is a trademark of Object Computing, Inc. and I hope I read the brand guidelines correctly for using its
name.
2. "GraphQL" is published by the [The GraphQL Foundation](https://graphql.org/)

