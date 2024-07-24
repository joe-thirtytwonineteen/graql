---
outline: deep
prev:
  text: 'Federation'
  link: './fetch'
# next:
#   text: 'Mutations'
#   link: './mutations'

---

> [!IMPORTANT]
> GraQL is prerelease, experimental, and a demonstration piece. Use at your own risk.

# Query Caching

## What is Query Caching?

Any given GraphQL query needs to be validated before it can be executed. This can be a significant
performance issue at runtime.

## How Does GraQL Help?

[Graphql-java provides an interface](https://www.graphql-java.com/documentation/execution/#query-caching)
that can be implemented to provide a cache of statements. [Netflix DGS provides an example solution](https://netflix.github.io/dgs/advanced/operation-caching/)
for a [Caffeine](https://github.com/ben-manes/caffeine)-based cache.

GraQL goes one step further, providing a simple implementation and automatically enabling it. 

In other words, you don't need to do anything: it's on by default.

## Configuring the Cache

GraQL will automatically cache the last one hundred (100) statements for a maximum one one hour. To 
configure this, the following properties can be changed in your micronaut `application.[properties|yaml|groovy]`
files.

The following example sets the maximum size to two hundred (200) statements with a time-to-live
of thirty (30) minutes.

```properties
graql.query-cache.max-size=200
graql.query-cache.expire-after-access=PT30M
```

## Replacing the Cache

If you'd like to entirely replace the cache, replace the `DefaultGraqlQueryCache` singleton
with your own implementation of [graphql-java's `PreparsedDocumentProvider`](https://www.graphql-java.com/documentation/execution/#query-caching)