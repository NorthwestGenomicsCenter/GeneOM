<template>
  <div class="app">
    <!-- Header -->
    <header class="header">
      <em class="pi pi-bars sidebar-icon" @click="toggleNavBar"/>
      <NuxtLink href="/" class="title">UW Multiomics Browser</NuxtLink>

<!--      <div class="title" @click="toggleNavBar">-->
<!--        UW Multiomics Browser-->
<!--      </div>-->
      <div class="search-container">
        <Search/>
<!--        <SearchButton>Search</SearchButton>-->
      </div>
      <div class="ui-user">
<!--        Login-->
        <StatusBarLogin username="Chia-Lin Wei" avatar-url="wei.jpg" />
      </div>
    </header>

    <div :class="{'main-content': true, 'collapsed': !isNavBarOpen}">
      <div class="navbar" v-if="isNavBarOpen">
        <nav>
          <ul>
            <li>
              <button class="sidebar-button" @click="toggleNavBar">
                <span class="icon">{{ isNavBarOpen ? '◀' : '▶' }}</span>
                <span class="text">{{ isNavBarOpen ? 'Hide' : '' }}</span>
              </button>
            </li>
            <li>
              <h3 class="menu-section">Assays</h3>
            </li>
            <li v-for="item in sidebarItems" :key="item.name">
              <NuxtLink :href="item.url" class="sidebar-button" :title="item.name">
                <span class="icon">{{ item.icon }}</span>
                <span class="text">{{ item.name }}</span>
              </NuxtLink>
            </li>

            <li>
              <h3 class="menu-section">Samples</h3>
            </li>
            <li>
              <NuxtLink :href="`/manage-samples`" class="sidebar-button" title="Manage Users">
                <span class="icon"></span>
                <span class="text">Manage Samples</span>
              </NuxtLink>
              <NuxtLink :href="`/manage-projects`" class="sidebar-button" title="Manage Users">
                <span class="icon"></span>
                <span class="text">Manage Projects</span>
              </NuxtLink>
            </li>

            <template v-if="isAdmin">
              <li>
                <h3 class="menu-section">Admin</h3>
              </li>
              <li>
                <NuxtLink :href="`/manage-users`" class="sidebar-button" title="Manage Users">
                  <span class="icon"></span>
                  <span class="text">Manage Users</span>
                </NuxtLink>
              </li>
            </template>

          </ul>
          {{ user }}
        </nav>
      </div>

      <div class="main-area">

        <section class="filter-bar">
          <span>Global Filters</span>
          <Button
              :label="geneButtonLabel"
              class="filter-item"
              rounded
              raised
              @click="toggleSidebar" />
          <Button
              label="0 Transcripts"
              class="filter-item"
              rounded
              raised />
          <Button
              label="0 Chromosomes"
              class="filter-item"
              rounded
              raised />
        </section>

        <slot/>
      </div>

      <div class="toolbar"></div>

    </div>

    <!-- Header -->
    <footer class="footer">
      <div class="container">
        <p>&copy; 2025 Northwest Genomics Center, University of Washington. All rights reserved.</p>
      </div>
    </footer>
  </div>

  <Sidebar
    v-model:visible="isSidebarOpen"
    position="right"
    class="sidebar-size"
    style="width: 60em !important;">
    <Filters />
  </Sidebar>
</template>

<script setup type="ts">
import { ref } from 'vue'
import { useGlobalStore } from "~/composables/useGlobal.ts";
import StatusBarLogin from "~/components/users/StatusBarLogin.vue";
import {useAuth} from "~/composables/useAuth.js";

const auth = useAuth();
const user = auth.user;
const isAdmin = computed(() => {
  return auth?.user?.value?.permissions?.includes("ROLE_ADMIN")
})

useHead({
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

const store = useGlobalStore();

const geneButtonLabel = computed(() => {
  const count = store.globalNav.genes.length;
  const pluralEnding = store.globalNav.genes.length === 1 ? "" : "s";
  return `${count} Gene${pluralEnding}`;
})

const isNavBarOpen = ref(true);
const toggleNavBar = () => {
  isNavBarOpen.value = !isNavBarOpen.value;
}

const sidebarItems = [
  { name: 'Assay', icon: '🧬', url: "/assay" },
  { name: 'Upload', icon: '🔬', url: "/import/ImportData" },
  { name: 'Gene Annotations', icon: '📝' },
  { name: 'Variant Calls', icon: '🔬' },
  { name: 'ChIP-seq Data', icon: '📊' },
  { name: 'RNA-seq Data', icon: '🧬' }
]

const isSidebarOpen = ref(false)
const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value
}

</script>

<style>
body {
  font-family: "Roboto", sans-serif;
  font-weight: 400;
  font-style: normal;
  margin: 0px;
  min-height: 100% !important;

  --p-button-primary-background: #56498e;
  --p-button-primary-border-color: #6b5cb2;
  --p-button-primary-hover-background: #4a407a;
  --p-button-primary-hover-border-color: #56498e;

  --toolbar-font-color: #333;
  --header-height: 5em;
  --footer-height: 4em;
}
</style>


<style scoped>
.app {
  padding: calc(var(--header-height)) 0;
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
}

.header {
  background-color: #4b2e83;
  color: white;
  display: flex;
  vertical-align: center;
  padding: 1rem;
  box-shadow: 0px 3px 15px -1px rgba(219,219,219,1);
  background-image: url("~/public/dna3.png"), linear-gradient(to right, #4b2e83, #4b2e83, #4b2e83, #4b2e83, #4b2e83, #4b2e83, #ba71ff, #ff71b8);;
  background-repeat: no-repeat;
  position: sticky;
  width: 100%;
  z-index: 20;
  margin-top: calc(var(--header-height) * -1);
  height: var(--header-height);
}

.footer {
  background-color: #b7a57a;
  color: white;
  text-align: center;
  height: var(--footer-height);
  margin-bottom: calc(var(--footer-height) * -1);
}

.footer .container {
  padding: 0;
  vertical-align: middle;
}

.main-content {
  display: grid;
  grid-template-columns: 14em 1fr 3.5rem;
  grid-template-rows: 1fr;
  gap: 0px 0px;
  grid-auto-flow: row;
  grid-template-areas:
    "navbar main-area toolbar";
  transition: grid-template-columns 0.3s ease;
  min-height: 100%;
}

.main-content.collapsed {
  grid-template-columns: 0 1fr 3.5rem; /* Adjust the grid for collapsed state */
}

.sidebar-size {
  width: 60em;
}

.toolbar-count {
  color: var(--toolbar-font-color);
  display: block;
  text-align: center;
}

.toolbar-item {
  border: 2px var(--p-button-primary-border-color) solid;
  border-radius: 5px;
  background-color: var(--p-button-primary-background);
  display: block;
  position: sticky;
  margin-bottom: 4px;
  margin-top: 4px;
}

.filter-icon {
  color: var(--toolbar-font-color);
  display: block;
  font-size: 1.5rem;
  margin: auto 5px;
  padding: 5px;
  text-align: center;
}

.navbar.collapsed {
  width: 0; /* Hide the sidebar */
  overflow: hidden; /* Prevent overflow when collapsed */
  transition: 0.3s ease; /* Add smooth transition */
}

.navbar {
  grid-area: navbar;
  transition: grid-template-columns 0.3s ease; /* Add smooth transition */
  box-shadow: 10px 0px 10px -3px rgba(0, 0, 0, 0.1);
  z-index: 1;
}

.navbar ul {
  padding: 0;
}

.navbar li {
  list-style: none;
  padding: 0;
  text-align: center;
}

.main-area {
  grid-area: main-area;
  background-color: #f0f0f0;
  padding: 1.5em;
}

.toolbar {
  grid-area: toolbar;
  background-color: #f0f0f0;
}

.search-container {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 0.5rem;
  margin-left: auto;
  margin-right:auto;
}

:deep(.search-container input) {
  width: 50vw;
  max-width: 800px;
}

.sidebar ul {
  list-style-type: none;
  padding: 0;
  display: grid;
  gap: 0.5rem;
}

.sidebar-button {
  align-items: center;
  background: none;
  border: none;
  color: #666666;
  cursor: pointer;
  display: flex;
  gap: 0.5rem;
  padding: 0.5rem;
  text-align: left;
  text-decoration: none;
  width: 100%;
}

.sidebar-icon {
  font-size: x-large;
  padding: 0.5em 0.75em 0.5em 0.25em;
}

.sidebar-collapsed .sidebar-button {
  justify-content: center;
}

.sidebar-button:hover {
  background-color: #e0e0e0;
}

.sidebar-collapsed .sidebar-button .text {
  display: none;
}

.sidebar-button .icon {
  font-size: 1.2rem;
  width: 1.5rem;
  text-align: center;
}

.title {
  color: white;
  display: block;
  font-size: 1.5em;
  font-weight: bold;
  margin-bottom: auto;
  margin-top: auto;
  text-decoration: none;
  user-select: none;
}

.ui-user {
  display: block;
  font-size: 1.0em;
  margin-top: auto;
  margin-bottom: auto;
  margin-right: 2em;
}

.menu-section {
  font-size: medium;
  text-align: left;
  padding-left: 1em;
  text-transform: uppercase;
  color: #666;
  font-weight: 900;
}

.filter-bar {
  margin-bottom: 1em;
  width: 100%;
  text-align: end;
}

.filter-item {
  margin-left: 0.5em;
}

</style>