<template>
  <div class="app">
    <!-- Header -->
    <header class="header">
      <div class="left">
        <em class="pi pi-bars sidebar-icon" @click="toggleNavBar" />
        <NuxtLink to="/" class="title">GENE-OM</NuxtLink>
      </div>
      <div class="center">
        <Search />
      </div>
      <div class="right">
        <StatusBarLogin username="Chia-Lin Wei" avatar-url="wei.jpg" />
      </div>
    </header>

    <!-- Main Content with 2 Columns -->
    <main class="main-layout">
      <NuxtLoadingIndicator />
      <nav class="sidebar">
        <Multiomics-Menu/>
      </nav>

      <section class="main-content">
        <slot />
      </section>
    </main>

    <!-- Footer -->
    <footer class="footer">
      <p>&copy; 2025 Northwest Genomics Center, University of Washington. All rights reserved.</p>
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
import {ref} from 'vue'
import {useGlobalStore} from "~/composables/useGlobal.ts";
import StatusBarLogin from "~/components/users/StatusBarLogin.vue";
import {useAuth} from "~/composables/useAuth.js";
import MultiomicsMenu from "~/components/common/MultiomicsMenu.vue";

const auth = useAuth();
const user = auth.user;
const isAdmin = computed(() => {
  return auth?.user?.value?.permissions?.includes("ROLE_ADMIN")
})

const baseUrl = useRuntimeConfig().app.baseURL;

useHead({
  style: [
    {
      innerHTML: `:root { --app-base-url: "${baseUrl}"; }`,
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
    {
      rel: 'stylesheet',
      href: "https://cdn.jsdelivr.net/npm/juicebox.js@2.4.8/dist/css/juicebox.css",
    },
  ]
});

const isNavBarOpen = ref(true);
const toggleNavBar = () => {
  isNavBarOpen.value = !isNavBarOpen.value;
}

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
  --footer-height: 3em;
}
</style>


<style scoped>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
}

.app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* Header layout with left, center, right zones */
.header {
  position: sticky;
  top: 0;
  z-index: 200;
  background-color: #4b2e83;
  background-image: url("@/public/dna3.png"), linear-gradient(to right, #4b2e83, #4b2e83, #4b2e83, #4b2e83, #4b2e83, #4b2e83, #ba71ff, #ff71b8);
  background-repeat: no-repeat;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  gap: 1rem;
}

.header > .left,
.header > .center,
.header > .right {
  display: flex;
  align-items: center;
}

.header .center {
  flex: 1;
  justify-content: center;
  width: 60em;
}

.header .right {
  gap: 1rem;
}

/* Main grid layout */
.main-layout {
  display: grid;
  grid-template-columns: 14em 1fr;
  flex: 1;
  min-height: 0;
}

.sidebar {
  background-color: #fff;
  position: sticky;
  top: var(--header-height);
  height: fit-content;
  align-self: start;
}

.main-content {
  background-color: #f0f0f0;
  padding: 1.5rem;
}

/* Footer styles */
.footer {
  background-color: #b7a57a;
  color: white;
  text-align: center;
}

/* Additional utility styles */
.title {
  color: white;
  text-decoration: none;
  font-size: 1.5rem;
  font-weight: bold;
}

.sidebar-icon {
  font-size: x-large;
  cursor: pointer;
}


</style>