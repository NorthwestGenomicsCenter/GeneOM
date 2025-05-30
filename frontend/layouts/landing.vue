<template>
  <div class="genomics-browser">
    <slot />
    <!-- Footer -->
    <footer class="footer">
      <div class="container">
        <p>&copy; 2025 Northwest Genomics Center, University of Washington. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {useGlobalStore} from "~/composables/useGlobal.ts";

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
  ],
});

const isSidebarOpen = ref(true)
const activeTab = ref('Genome Overview')

const sidebarItems = [
  { name: 'Assay', icon: '🧬', url: "/assay" },
  { name: 'Upload', icon: '🔬', url: "/import/ImportData" },
  { name: 'Gene Annotations', icon: '📝' },
  { name: 'Variant Calls', icon: '🔬' },
  { name: 'ChIP-seq Data', icon: '📊' },
  { name: 'RNA-seq Data', icon: '🧬' }
]

const tabs = [
  'Genome Overview',
  'Genes',
  'Variants',
  'ChIP-seq',
  'RNA-seq'
]

const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value
}

// AutoComplete Search
const autocompleteValue = ref("");
const autocompleteSuggestions = ref([]);

const search = async (event) => {
  let res = await $fetch(`/api/search/${event.query}`)
  autocompleteSuggestions.value = res
}

// Global State
let globalStore = useGlobalStore();
let globalNav = computed(() => globalStore.globalNav);
let changeGene = globalStore.changeGene;
</script>

<style>
body {
  font-family: "Roboto", sans-serif;
  font-weight: 400;
  font-style: normal;
  margin: 0px;

  --p-button-primary-background: #b791ff;
  --p-button-primary-border-color: #a474ff;
  --p-button-primary-hover-background: #773fec;
  --p-button-primary-hover-border-color: #723de0;
}
</style>


<style scoped>
.genomics-browser {
  display: grid;
  grid-template-rows: auto 1fr auto;
  min-height: 100vh;
  font-family: Arial, sans-serif;
}

.header {
  background-color: white;
  color: white;
  display: flex;
  vertical-align: center;
  padding: 1rem;
  box-shadow: 0px 3px 15px -1px rgba(219,219,219,1);
  background-image: url("dna3.png");
  background-repeat: no-repeat;
}

.header .container {
  display: grid;
  grid-template-columns: 1fr auto;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.search-container {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 0.5rem;
  margin-left: auto;
  margin-right:auto;
}

.search-input {
  width: 50vw;
  max-width: 800px;
}

:deep(.search-input>input) {
  width: 100%;
}

.search-button {
  background-color: white;
  color: #4b2e83;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}

.main-content {
  display: grid;
  grid-template-columns: auto 1fr;
}

.sidebar {
  background-color: #f0f0f0;
  width: 250px;
  padding: 1rem;
  transition: all 0.3s ease;
  position: relative;
  box-shadow: 3px 0px 5px -1px rgba(219,219,219,1)
}

.sidebar-collapsed {
  width: 60px;
}

.sidebar-toggle {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 5px;
}

.sidebar ul {
  list-style-type: none;
  padding: 0;
  display: grid;
  gap: 0.5rem;
}

.sidebar-button {
  width: 100%;
  text-align: left;
  padding: 0.5rem;
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
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
  display: block;
  font-size: 1.5em;
  margin-top: auto;
  margin-bottom: auto;
  font-weight: bold;
  user-select: none;
}

.ui-user {
  display: block;
  font-size: 1.0em;
  margin-top: auto;
  margin-bottom: auto;
  margin-right: 2em;
}

.browser-area {
  padding: 1rem;
  display: grid;
  grid-template-rows: auto 1fr;
  gap: 1rem;
}

.browser-controls {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1rem;
}

.control-button {
  padding: 0.5rem;
  background-color: #f0f0f0;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
}

.zoom-controls {
  display: grid;
  grid-template-columns: repeat(4, auto);
  gap: 0.5rem;
  justify-content: end;
}

.tabs-container {
  border: 1px solid #ccc;
  border-radius: 4px;
  display: grid;
  grid-template-rows: auto 1fr;
}

.tabs-header {
  display: grid;
  grid-auto-flow: column;
  grid-auto-columns: 1fr;
  border-bottom: 1px solid #ccc;
}

.tab-button {
  padding: 0.5rem 1rem;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  cursor: pointer;
}

.tab-button.active {
  border-bottom-color: #4b2e83;
  color: #4b2e83;
}

.tab-content {
  padding: 1rem;
  min-height: 400px;
}

.footer {
  background-color: #b7a57a;
  color: white;
  text-align: center;
}

@media (max-width: 768px) {
  .header .container {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .main-content {
    grid-template-columns: auto 1fr;
  }

  .sidebar {
    position: fixed;
    top: 0;
    left: 0;
    height: 100%;
    z-index: 1000;
    transform: translateX(-100%);
    transition: transform 0.3s ease-in-out;
  }

  .sidebar:not(.sidebar-collapsed) {
    transform: translateX(0);
  }

  .sidebar-collapsed {
    transform: translateX(-60px);
  }

  .browser-controls {
    grid-template-columns: 1fr;
  }

  .zoom-controls {
    justify-content: start;
  }
}
</style>