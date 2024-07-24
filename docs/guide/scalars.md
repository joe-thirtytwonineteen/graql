---
outline: deep
prev:
  text: 'Fetch'
  link: './fetch'
next:
  text: 'Federation'
  link: './federation'

---

> [!IMPORTANT]
> GraQL is prerelease, experimental, and a demonstration piece. Use at your own risk.

# Scalars

## What Are Scalars?

Scalars are the lowest-level types in a GraphQL schema. You're likely already familiar with a few:
`Boolean`, `String`, `Int`, etc.

To build a robust API, you'll likely need to define more, such as `Date` or `EmailAddress`.

[graphql-java](https://www.graphql-java.com/documentation/scalars/) provides great
[documentation and an interface](https://www.graphql-java.com/documentation/scalars/) for doing this.

GraQL builds on this by allowing you to annotate any implementation of the 
`graphql-java` `graphql.schema.Coercing` interface as a `@GraQLScalar` to automatically register it
with the underlying `GraphQL` schema.

## Example

For a given schema definition declaring a DateTime scalar and using it within a `ToDoDTO` input type:

```graphql
scalar DateTime

input ToDoDTO {
    title: String!
    author: String!
    dueDate: DateTime!
}
```

GraQL includes a scalar for `DateTime` with a very straightforward implementation:

```kotlin
@GraQLScalar
class DateTime: Coercing<LocalDateTime, String> {

    override fun serialize(input: Any): String {
        if (input is LocalDateTime) {
            return input.format(DateTimeFormatter.ISO_DATE_TIME)
        } else {
            throw CoercingSerializeException("Not a valid DateTime")
        }
    }

    override fun parseValue(input: Any): LocalDateTime {
        return LocalDateTime.parse(input.toString(), DateTimeFormatter.ISO_DATE_TIME)
    }

    override fun parseLiteral(input: Value<*>, variables: CoercedVariables, graphQLContext: GraphQLContext, locale: Locale): LocalDateTime? {
        if (input is StringValue) {
            return LocalDateTime.parse((input).getValue(), DateTimeFormatter.ISO_DATE_TIME)
        }

        throw CoercingParseLiteralException("Value is not a valid ISO date time")
    }

    override fun valueToLiteral(input: Any, graphQLContext: GraphQLContext, locale: Locale): Value<*> {
        return StringValue(this.serialize(input))
    }
    
}
```

