<template>
  <div class="app">
    <!-- Header -->
    <header class="header">
      <div class="left">
        <em class="pi pi-bars sidebar-icon" @click="toggleNavBar" />
        <NuxtLink to="/" class="title">
          <NuxtImg src="/banner_logo.png" height="40" class="banner-logo"/>
        </NuxtLink>
      </div>
      <div class="center">
        <Search />
      </div>
      <div class="right">
        <StatusBarLogin />
      </div>
    </header>

    <!-- Main Content with 2 Columns -->
    <main class="main-layout">
      <NuxtLoadingIndicator />
      <div class="sidebar-trigger"></div>
      <nav class="sidebar" :class="{ collapsed: !isNavBarOpen }">
        <Multiomics-Menu/>
      </nav>

      <section class="main-content">
        <template v-if="user.isAuthenticated">
          <slot />
        </template>
        <template v-else>
          <Index />
        </template>
      </section>
    </main>

    <Toast />

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
import {useAuth} from "~/composables/useAuth.js";

import Index from "~/pages/index.vue";
import MultiomicsMenu from "~/components/common/MultiomicsMenu.vue";
import StatusBarLogin from "~/components/users/StatusBarLogin.vue";

const auth = useAuth();
const user = auth.user;
const { scheduleLogout } = auth;
// scheduleLogout();

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
    {
      rel: 'stylesheet',
      href: "https://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css",
    }
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
  margin: 0;
  min-height: 100% !important;

  /* ── Primary ── */
  --p-button-primary-background: #56498e;
  --p-button-primary-border-color: #6b5cb2;
  --p-button-primary-color: #ffffff;

  /* ── Hover ── */
  --p-button-primary-hover-background: #4a407a;
  --p-button-primary-hover-border-color: #56498e;
  --p-button-primary-hover-color: #ffffff;

  /* ── Active (pressed) ── */
  --p-button-primary-active-background: #3d3466;
  --p-button-primary-active-border-color: #4a407a;
  --p-button-primary-active-color: #ffffff;

  /* ── Focus ring ── */
  --p-button-primary-focus-ring-color: #56498e;
  --p-button-primary-focus-ring-offset: 2px;
  --p-button-primary-focus-ring-shadow: none;
  --p-button-primary-focus-ring-width: 2px;

  /* ── Contrast (e.g. on dark surfaces) ── */
  --p-button-primary-contrast-color: #ffffff;

  /* ── Disabled ── */
  --p-button-primary-disabled-background: #b8b3d4;
  --p-button-primary-disabled-border-color: #b8b3d4;
  --p-button-primary-disabled-color: #f3f2f9;

  /* ── Outlined Primary ── */
  --p-button-outlined-primary-color: #56498e;
  --p-button-outlined-primary-border-color: #56498e;
  --p-button-outlined-primary-background: transparent;

  --p-button-outlined-primary-hover-border-color: #d6d0ff;
  --p-button-outlined-primary-hover-background: #d6d0ff;

  --p-button-outlined-primary-active-color: #ffffff;
  --p-button-outlined-primary-active-border-color: #4a407a;
  --p-button-outlined-primary-active-background: #4a407a;

  --p-button-outlined-primary-focus-ring-color: #56498e;
  --p-button-outlined-primary-focus-ring-offset: 2px;
  --p-button-outlined-primary-focus-ring-shadow: none;
  --p-button-outlined-primary-focus-ring-width: 2px;

  --p-button-outlined-primary-disabled-color: #b8b3d4;
  --p-button-outlined-primary-disabled-border-color: #b8b3d4;
  --p-button-outlined-primary-disabled-background: transparent;

  /* ── Text Primary ── */
  --p-button-text-primary-color: #56498e;
  --p-button-text-primary-background: transparent;

  --p-button-text-primary-hover-color: #4a407a;
  --p-button-text-primary-hover-background: #eeedf7;

  --p-button-text-primary-active-color: #3d3466;
  --p-button-text-primary-active-background: #dddcf0;

  --p-button-text-primary-focus-ring-color: #56498e;
  --p-button-text-primary-focus-ring-offset: 2px;
  --p-button-text-primary-focus-ring-shadow: none;
  --p-button-text-primary-focus-ring-width: 2px;

  --p-button-text-primary-disabled-color: #b8b3d4;
  --p-button-text-primary-disabled-background: transparent;

  --toolbar-font-color: #333;
  --header-height: 5em;
  --footer-height: 3em;

  /* shadows */
  --shadow-s: 0 1px 2px #00000030, /* dark shadow */ 0 2px 4px #00000015; /* soft shadow */
  --shadow-m: 0 2px 4px #00000030, /* dark shadow */ 0 4px 8px #00000015; /* soft shadow */
  --shadow-l: 0 4px 6px #00000030, /* dark shadow */ 0 6px 10px #00000015; /* soft shadow */
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

.banner-logo {
  padding-left: 0.75em;
}

/* Main flex layout */
.main-layout {
  display: flex;
  flex: 1;
  min-height: 0;
}

.sidebar {
  --sidebar-width: 12em;
  --sidebar-peek: 3em;

  background-color: #fff;
  position: sticky;
  top: var(--header-height);
  height: fit-content;
  align-self: start;

  flex: 0 0 var(--sidebar-width);
  width: var(--sidebar-width);
  overflow: hidden;
  white-space: nowrap;

  display: flex;
  justify-content: flex-end;

  transition:
      flex-basis 0.3s ease-in-out,
      width 0.3s ease-in-out,
      opacity 0.25s ease-in-out,
      padding 0.3s ease-in-out;
}

.sidebar > * {
  flex: 0 0 var(--sidebar-width);
  width: var(--sidebar-width);
}

.sidebar.collapsed {
  flex-basis: 0;
  width: 0;
  opacity: 0;
  padding: 0;
  pointer-events: none;
}

.sidebar-trigger:hover ~ .sidebar.collapsed,
.sidebar.collapsed:hover {
  flex-basis: var(--sidebar-peek);
  width: var(--sidebar-peek);
  opacity: 1;
  pointer-events: auto;
  transition-delay: 0.1s;
}

.main-content {
  background-color: #f0f0f0;
  padding: 0.5em;
  min-width: 0;
  overflow: hidden;
  flex: 1 1 0;
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