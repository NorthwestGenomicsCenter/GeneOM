<template>
  <div class="multiomics-browser">
    <h1>Questions for future development</h1>

    <section class="assays">
      <ul>
        <li>Who is the end user of this application</li>
        <li>Should all processing happen in browser to protect user data?</li>
        <li>Do we process raw data into final form for visualization, or provide pipelines so external users can BYOD (bring your own data)</li>
      </ul>
    </section>
  </div>
</template>

<script setup lang="ts">
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

li {
  font-size: xx-large;
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