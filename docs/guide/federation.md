---
outline: deep
prev:
  text: 'Scalars'
  link: './scalars'
next:
  text: 'Query Caching'
  link: './caching'

---

> [!IMPORTANT]
> GraQL is prerelease, experimental, and a demonstration piece. Use at your own risk.

# Federation

## What is Federation?

Federation allows you to publish multiple GraphQL APIs that work together as one. In other words,
multiple microservices, monoliths, or other architectures we haven't yet devised can "join" 
their schemas into one "superschema."

## How Do I Federate?

Easily! Apollo provides the [federation-jvm](https://github.com/apollographql/federation-jvm) module,
built on top of [graphql-java](https://www.graphql-java.com/). 

It does all of the heavy lifting. All we need to do is add directives to our schema that state 
which types are entry points from the supergraph into our schema.

In this example, we're registering `ToDo` as such a type by adding the `@key` directive

```
type ToDo @key(fields: "id") { # <4>
    id: ID!
    title: String!
    completed: Boolean!
    dateCompleted: DateTime
    author: Author!
}
```

## How Does This Work?

GraQL automatically registers any necessary `DataFetcher`s and `TypeResolver`s needed by a federation router
through its `GraQLFederationProvider` and `GraQLFederatedEntityResolver` classes.
