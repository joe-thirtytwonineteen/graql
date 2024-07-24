---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: "GraQL"
  # text: "GraphQL for micronaut, minus the boilerplate"
  tagline: GraphQL for micronaut® with near-zero boilerplate
  actions:
    - theme: brand
      text: Quickstart
      link: /introduction/quickstart
    - theme: alt
      text: GraphQL Guide
      link: /guide/gettingstarted
    - theme: alt
      text: Real-World Development
      link: /development/exceptions
    - theme: alt
      text: How it Works
      link: /internals/scanning

features:
  - title: Terse
    details: GraQL is a "just-enough" controller-tier replacement for manual GraphQL configuration.
  - title: Kind
    details: GraQL doesn't hide exceptions from logs and relies on your existing micronaut features, like input validation. 
  - title: Humble
    details: Almost any piece can be replaced via micronaut dependency injection.
---

