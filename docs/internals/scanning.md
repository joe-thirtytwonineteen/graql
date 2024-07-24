---
outline: deep
prev:
  text: 'Security and Threads'
  link: '../development/security'
next:
    text: 'Customizing Delegates'
    link: './delegates'
---

# Startup Scanning

GraQL does add slight overhead to the startup of your micronaut application. At startup, it:


1. Uses the underlying micronaut application context to inspect any registered beans of stereotypes `GraQLComponent` and `GraQLScalar`. 
2. Creates delegates for data fetches (queries, mutations, fetches), data loaders, and exception handlers.
3. Registers scalars.
4. Uses `GraQLFactory` to create the underlying `graphql.GraphQL` for micronaut GraphQL integration.

## GraQLComponent Registration

### Delegates

Delegates form the core of GraQL. 

During the scanning phase, annotations like `@GraQLQuery` are used
to create delegate instances that receive input from GraphQL and invoke methods on your
`@GraQLComponent`s.

A central (and replaceable/extensible!) `GraQLDelegateFactory` interface is responsible
for stating which annotations are recognized and providing a `GraQLDelegationConfigurator`
implementation for each annotation.

This allows you to extend [GraQL with new delegates, annotations, and your own implementation
of delegation behavior](./delegates).

### Exception Handlers

Components annotated with `GraQLExceptionHandler` are registered and provided to the `GraQLGlobalExceptionHandler`.

If a GraphQL operation throws an exception, the global exception handler checks for an appropriate delegate. 

If one exists for the exception type, it's then invoked.

## GraQLScalar Registration

GraQLScalar scanning simply confirms that annotated components implement the correct 
interface (`graphql.schema.Coercing`). Once validated, they're available to the internal
`GraQLFactory` for configuration.

