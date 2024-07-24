import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  base: "/graql/",
  title: "GraQL",
  description: "GraphQL for micronaut, minus the boilerplate",
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: 'Home', link: '/' },
      /*
      { text: 'Examples', link: '/markdown-examples' }

         */
    ],

    sidebar: [
      {
        text: 'Introduction',
        items: [
          { text: 'Quickstart', link: '/introduction/quickstart' },
          { text: 'About GraQL', link: '/introduction/about' },
        ]
      },
      {
        text: 'GraphQL Implementation',
        items: [
          { text: 'Getting Started', link: '/guide/gettingstarted' },
          { text: 'Queries', link: '/guide/queries' },
          { text: 'Mutations', link: '/guide/mutations' },
          { text: 'Data Loaders (N+1)', link: '/guide/dataloaders' },
          { text: 'Fetch', link: '/guide/fetch' },
          { text: 'Scalars', link: '/guide/scalars' },
          { text: 'Federation', link: '/guide/federation' },
          { text: 'Query Caching', link: '/guide/caching' },
        ]
      },
      {
        text: 'Real-World Development',
        items: [
          { text: 'Exception Handling', link: '/development/exceptions' },
          { text: 'Input Validation', link: '/development/validation' },
          { text: 'Security and Threads', link: '/development/security' },
        ]
      },
      {
        text: 'How it Works',
        items: [
          { text: 'Startup Scanning', link: '/internals/scanning' },
          { text: 'Customizing Delegates', link: '/internals/delegates' },
        ]
      },

    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/joe-thirtytwonineteen/graql' }
    ]
  }
})
