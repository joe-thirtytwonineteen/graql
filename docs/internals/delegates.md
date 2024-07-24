---
outline: deep
prev:
    text: 'Startup Scanning'
    link: './scanning'
---

# Customizing Delegates

GraQL isn't perfect and you may find places it doesn't suit your needs. Therefore,
you're free to extend or replace almost all of its functionality.

This section focuses on how to replace delegates, i.e. "How do I change how `@GraQLQuery` and 
`@GraQLMutation` work?" or "How do I add my own new annotation?"

## About GraQL Delegates

GraQL delegates are the concrete implementations of `graphql-java` interfaces responsible for 
handling inbound GraphQL requests. 

For example, a `GraQLDelegatingQuery` implementation must meet the contract defined in
`graphql-java`'s [`graphql.schema.DataFetcher`](https://javadoc.io/doc/com.graphql-java/graphql-java/latest/graphql/schema/DataFetcher.html).

In the following section, we'll trace how GraQL creates delegate implementations, starting
with a factory used during [startup scanning](./scanning.md) and ending with the implementation
of the delegate created by the `@GraQLQuery` annotation.


## The `GraQLDelegationFactory`

`GraQLDelegationFactory` is an interface that requires an implementor to provide a map
of annotation classes to `GraQLDelegationConfigurator` interfaces. 

The default implementation (narrowed below to focus on `...Query...`), shows how annotations
are related to singleton `GraQLDelegationConfigurator` instances, revealing that you're 
free to replace individual configurators at will:

```kotlin
open class DefaultGraQLDelegationFactory(
    @Named("graQLQueryConfigurator") private val queryConfigurator: GraQLDelegationConfigurator<GraQLQuery>,
    /* ...other configurators injected via constructor.... */
    
) : GraQLDelegationFactory {

    override val delegateConfigurators = mapOf(
        GraQLQuery::class to queryConfigurator,
        /* ...other configurators.... */
    )

}
```

## `GraQLDelegateConfigurator`s

The `GraQLDelegateConfigurator` is an interface where implementors are responsible for receiving
various inputs (a micronaut `BeanDefinition`, a reference to an annotated `Method`, and the
`GraQL` annotation itself) and supplying one-to-many `GraQLDelegate` classes in response.

In our `...Query..` example, we've commented the default implementation to below to explain its
inner workings and provide guidance on how they could be extended or changed:

```kotlin
@Named("graQLQueryConfigurator")
class DefaultGraQLQueryConfigurator(
    private val beanContext: BeanContext,
    private val parameterMapper: GraQLRequestParameterMapper,
    private val exceptionHandler: GraQLGlobalExceptionHandler,
) : GraQLDelegationConfigurator<GraQLQuery> {
    override fun createDelegate(beanDefinition: BeanDefinition<*>, method: Method, a: Annotation): List<GraQLDelegate> {
        val annotation = a as GraQLQuery
        
        /* 
        Step 1: Validate any assumptions about the annotated method. For example,
        a query delegate can't handle a @GraQLQuery method with more than one
        parameter.
        */
        if ( method.parameters.size != 1 ) {
            throw GraQLDelegationException("Cannot create GraQLQuery delegate for ${method.declaringClass.simpleName}::${method.name}: it does not require exactly one parameter.")
        }
        
        /*
        Step 2: Gather any information about the method/context needed to supply
        our reasonable defaults. For example, @GraQLQuery assumes that any GraphQL
        input name matches the name of the request parameter.
        */
        val requestParameter = method.parameters.first()

        /*
        Step 3: Provide any necessary delegates. @GraQLQuery is simple and provides
        one (BatchedFetch is not: it provides a minimum of two, one of which is actually
        a factory to provide per-request data loaders!).
        */
        return listOf(
            DefaultGraQLDelegatingQuery(
                exceptionHandler = exceptionHandler,
                /*
                Apply the default "if no name is provided, name the query after
                the method" rule. 
                */
                name = when {
                    annotation.name.isBlank() -> method.name
                    else -> annotation.name
                },
                method = method,
                target = beanContext.getBean(beanDefinition),
                /*
                Apply the default "if no input name is provided, assume it 
                matches the GraphSQL schema" rule. 
                */
                argumentName = when {
                    annotation.input.isBlank() -> requestParameter.name
                    else -> annotation.input
                },
                requestType = requestParameter.type,
                requestParameterMapper = parameterMapper
            )
        )
    }
}
```

## Delegate Implementation

Remember: GraQL delegates are the concrete implementations of `graphql-java` interfaces responsible for
handling inbound GraphQL requests.

When the default `DefaultGraQLQueryConfigurator` creates an instance `DefaultGraQLDelegatingQuery`,
it's providing an implementation of `graphql-java`'s [`graphql.schema.DataFetcher`](https://javadoc.io/doc/com.graphql-java/graphql-java/latest/graphql/schema/DataFetcher.html)
that has a reference to your target micronaut component/annotated method with the intent of:

1. Mapping any request parameters
2. Invoking your target method
3. Handling any exceptions

Its abbreviated source code is annotated below to explain its workings:

```kotlin
class DefaultGraQLDelegatingQuery(
    /* ...constructor arguments passed in by the GraQLDelegateConfigurator... */
): GraQLDelegatingQuery<Any>, AbstractGraQLDelegate(exceptionHandler) {

    /*
    get(env: DataFetchingEnvironment): Any? is the DataFetcher contract
    that must be met
    */
    override fun get(env: DataFetchingEnvironment): Any? {
        /*
        Retrieve any input argument passed in from GraphQL
        */
        val arg = env.getArgument<Any>(argumentName)

        /*
        If it exists, use our provided GraQLRequestParameterMapper
        implementation to map it to an instance of the input type
        required by our target method.
        */
        val req = when {
            arg == null -> null
            else -> requestParameterMapper.map( arg, requestType )
        }

        /*
        With use of the GraQLGlobalExceptionHandler to handle any known exception
        types (rethrowing if unknown), invoke our target method.
        */
        return withExceptionHandling(
            {
                method.invoke( target, req )
            },
            /*
            For unhandled exceptions, provide a lambda that will generate 
            exception text to log.
            */
            { "Error delegating to ${target::class.simpleName}::${method.name}: your client should get an error message. Exception is logged." }
        )
    }
}
```
