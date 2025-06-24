<template>
  <section class="filter-bar">
    <span>Global Filters</span>
    <Button
        :label="geneButtonLabel"
        class="filter-item"
        rounded
        raised
        @click="toggleSidebar"/>
    <Button
        label="0 Transcripts"
        class="filter-item"
        rounded
        raised/>
    <Button
        label="0 Chromosomes"
        class="filter-item"
        rounded
        raised/>
  </section>
</template>
<script setup lang="ts">
import {useGlobalStore} from "~/composables/useGlobal.js";

defineOptions({
  name: 'FiltersBar'
})

const store = useGlobalStore();

const geneButtonLabel = computed(() => {
  const count = store.globalNav.genes.length;
  const pluralEnding = store.globalNav.genes.length === 1 ? "" : "s";
  return `${count} Gene${pluralEnding}`;
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

.filter-bar {
  margin-bottom: 1em;
  width: 100%;
  text-align: end;
}

.filter-item {
  margin-left: 0.5em;
}

</style>