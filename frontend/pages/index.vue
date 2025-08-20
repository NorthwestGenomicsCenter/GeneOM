<template>
  <div class="multiomics-browser">
    <h1>NWGC Multiomics Browser</h1>
    <section class="getting-started">
      <h2>Getting Started</h2>
      <p>Select an assay from the right menu to begin.</p>
    </section>

    <section class="assays">
      <h2>Available Assays</h2>
      <ul>
        <li>Differential Expression</li>
        <li>Differential Peaks</li>
        <li>IGV Viewer (with structural tracks)</li>
      </ul>
    </section>

    <section class="filters">
      <h2>Global Filters</h2>
      <p>Apply filters to refine data across all assay types.</p>
    </section>
  </div>

</template>

<script setup lang="ts">
const auth = useAuth();

definePageMeta({
  layout: 'default'
})

</script>

<style scoped>
.multiomics-browser {
  font-family: Arial, sans-serif;
  line-height: 1.6;
  color: #333;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  color: #2c3e50;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

h2 {
  color: #3498db;
  margin-top: 20px;
}

ul {
  padding-left: 20px;
}

section {
  margin-bottom: 25px;
}
</style>