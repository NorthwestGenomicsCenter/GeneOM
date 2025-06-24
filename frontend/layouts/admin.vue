<template>
  <Header />
  <div
    class="layout"
    :class="{'sb-expanded': navExpanded }">
    <aside class="main-nav">
      <nav>
        <ul>
          <li>
            <a href="#" class="active">
              <i class="pi pi-database"></i>
              <span>Dashboard</span>
            </a>
          </li>
          <li>
            <a href="#">
              <i class="pi pi-user"></i>
              <span>Users</span>
            </a>
          </li>
          <li>
            <a href="#">
              <i class="pi pi-server"></i>
              <span>Server</span>
            </a>
          </li>
          <li @click="toggleNavExpanded">
            <a href="#" data-resize-btn>
              <i class="pi pi-angle-double-left"></i>
              <span>Collapse</span>
            </a>
          </li>
        </ul>
      </nav>
    </aside>

    <main class="main-area">
      <slot />
    </main>

    <aside class="right-nav">
      <nav>
        <ul>
          <li>
            <a href="#" class="active">
              <em class="pi pi-database"/>
            </a>
          </li>
        </ul>
      </nav>
    </aside>

  </div>
</template>

<script setup lang="ts">
const navExpanded = ref(false);
const toggleNavExpanded = () => {
  navExpanded.value = !navExpanded.value;
}

const baseUrl = useRuntimeConfig().app.baseURL;

useHead({
  style: [
    {
      children: `:root { --app-base-url: "${baseUrl}"; }`,
    },
  ],
  link: [
    {
      rel: 'preconnect',
      href: 'https://fonts.googleapis.com',
    },
    {
      rel: 'preconnect',
      href: 'https://fonts.gstatic.com',
      crossorigin: 'anonymous',
    },
    {
      rel: 'stylesheet',
      href: 'https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&display=swap',
    },
  ]
});

</script>


<style>
body {
  --sb-width: 5rem;
  --right-nav-bar-width: 3.75em;
  --header-height: 5em;
}
</style>

<style scoped>
.sb-expanded {
  --sb-width: 12.5rem;
}

.main-area {
  padding-top: calc(var(--header-height) + 1em);
}

h1 {
  font-size: 1.5rem;
  font-weight: bold;
}

p {
  margin-bottom: 1.5rem;
}

aside.right-nav {
  position: fixed;
  inset: 0 0 0 auto;
  width: var(--right-nav-bar-width);
  background-color: #f0f4f9;
  transition: width 0.5s ease-in-out;
  margin-top: var(--header-height);
}

aside.main-nav {
  position: fixed;
  inset: 0 auto 0 0;
  width: var(--sb-width);
  background-color: #1D0040;
  transition: width 0.5s ease-in-out;
  margin-top: var(--header-height);
}

nav {
  height: 100%;
  /* border: 1px solid #000; */
}

nav ul {
  list-style: none;
  height: 100%;
  display: flex;
  flex-flow: column;
  gap: 0.25rem;
  padding: 0 1em;
}

.main-nav nav li:last-child {
  margin-top: auto;
  padding-bottom: 2em;
}

.main-nav nav a {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  padding: 0.625rem 0.875rem;
  font-size: 1.25rem;
  line-height: 1;
  color: #fff;
  text-decoration: none;
  border-radius: 0.375rem;
  transition: background-color 0.5s ease-in-out, color 0.5s ease-in-out;
}

.right-nav nav a {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  padding: 0.625rem 0.875rem;
  font-size: 1.25rem;
  line-height: 1;
  color: #172940;
  text-decoration: none;
  border-radius: 0.375rem;
  transition: background-color 0.5s ease-in-out, color 0.5s ease-in-out;
}

.main-nav nav a.active,
.main-nav nav a:hover,
.main-nav nav a:focus-visible {
  outline: none;
  color: #b366fc;
  background-color: #fff;
}

.main-nav nav a span {
  font-size: 0.875rem;
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.3s ease-in-out, visibility 0.3s ease-in-out;
}

.sb-expanded nav a span {
  opacity: 1;
  visibility: visible;
}

.sb-expanded aside .bx-chevrons-right {
  rotate: 180deg;
}

main {
  margin-left: 5rem;
  padding: 1rem 2rem;
  transition: margin-left 0.5s ease-in-out;
}

@media (min-width: 768px) {
  main {
    margin-left: var(--sb-width);
  }
}

</style>