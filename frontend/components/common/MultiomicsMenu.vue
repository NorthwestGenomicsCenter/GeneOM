<template>
  <ul>
<!--    <li>-->
<!--      <button class="sidebar-button" @click="toggleNavBar">-->
<!--        <span class="icon">{{ isNavBarOpen ? '◀' : '▶' }}</span>-->
<!--        <span class="text">{{ isNavBarOpen ? 'Hide' : '' }}</span>-->
<!--      </button>-->
<!--    </li>-->
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
      <h3 class="menu-section">Manage</h3>
    </li>
    <li>
      <NuxtLink :href="`/manage/samples`" class="sidebar-button" title="Manage Users">
        <span class="icon"></span>
        <span class="text">Samples</span>
      </NuxtLink>
      <NuxtLink :href="`/manage/cohorts`" class="sidebar-button" title="Manage Cohorts">
        <span class="icon"></span>
        <span class="text">Cohorts</span>
      </NuxtLink>
      <NuxtLink :href="`/import/ImportExternalData`" class="sidebar-button" title="Import External">
        <span class="icon"></span>
        <span class="text">Import External Data</span>
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
</template>

<script setup type="ts">
import {useAuth} from "~/composables/useAuth";

const auth = useAuth();
const user = auth.user;
const isAdmin = computed(() => {
  return auth?.user?.value?.permissions?.includes("ROLE_ADMIN")
})

defineOptions({
  name: 'Multiomics-Menu'
})

const sidebarItems = [
  { name: 'Assay', icon: '🧬', url: "/assay" },
  { name: 'Upload', icon: '🔬', url: "/import/ImportData" },
  { name: 'Gene Annotations', icon: '📝' },
  { name: 'Variant Calls', icon: '🔬' },
  { name: 'ChIP-seq Data', icon: '📊' },
  { name: 'RNA-seq Data', icon: '🧬' }
]

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

.navbar ul {
  padding: 0;
}

.navbar li {
  list-style: none;
  padding: 0;
  text-align: center;
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

.sidebar-collapsed .sidebar-button {
  justify-content: center;
}

.sidebar-button:hover {
  background-color: #e0e0e0;
}

.sidebar-button .icon {
  font-size: 1.2rem;
  width: 1.5rem;
  text-align: center;
}

.menu-section {
  font-size: medium;
  text-align: left;
  padding-left: 1em;
  text-transform: uppercase;
  color: #666;
  font-weight: 900;
}

</style>