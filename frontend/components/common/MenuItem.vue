<template>
  <li>
    <NuxtLink
      :to="to"
      class="sidebar-button"
      :class="{'current-page': isActive }"
      :title="text">
      <span
        v-if="icon !== null && icon !== ''"
        class="icon"
        v-html="icon"/>
      <span class="text">{{ text }}</span>
    </NuxtLink>
  </li>
</template>

<script setup lang="ts">
const router = useRouter();

interface Props {
  to: string
  text: string
  icon: string
}

const props = defineProps<Props>()

const isActive = computed(() => {
  const to = props.to;
  const fullPath = router.currentRoute?.value?.fullPath;
  if (to == null || fullPath == null) return false;

  return fullPath.startsWith(to);
})

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
.current-page {
  background-color: #e4ebee !important;
}

li {
  padding: 0.125em 0.5em 0.125em 0.5em !important;
}

a {
  border-radius: 8px;
}

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
  background-color: #f6f8f9;
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