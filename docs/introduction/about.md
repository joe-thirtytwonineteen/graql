---
outline: deep
next:
    text: 'Getting Started'
    link: '/guide/gettingstarted'
---

# About GraQL

GraQL is a work in progress. I'm adding to it as I need features for an upcoming open-source+commercial
product. 

It's also being used as a demonstration of my individual competencies in open-source 
contribution and documentation, as well as a platform to explore Kotlin and micronaut.

## Philosophy

GraQL should:

1. Not replace or require changes to existing, well-designed service-tier business logic (i.e., It's just a controller).
2. **Be humble**. Any of its internals should be replaceable through micronaut dependency injection. 
3. **Help developers self-care**. It should not mask exceptions or make debugging harder!



## Project Status

GraQL is prerelease. I wouldn't use it in production (yet!). 

It has a few outstanding large gaps in functionality:

1. Pagination
2. GraphQL subscription support

