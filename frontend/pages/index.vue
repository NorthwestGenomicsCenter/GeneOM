<template>
  <div class="multiomics-browser">
    <h1>GENE-OM</h1>
    <section v-if="!isAuthenticated" class="getting-started">
      <h2>Getting Started</h2>
      <ul>
        <li>Contact system administrator to create an account</li>
        <li>Login to the application</li>
        <li>Load your data</li>
        <li>Create your analysis</li>
        <li>Start Exploring</li>
      </ul>
    </section>

    <section v-if="isAuthenticated" class="assays">
      <p>Welcome Back {{user.firstName}}</p>
      <h2>Continue Working on Recent Assays</h2>
      <RecentAnalysisTable />
    </section>
  </div>

</template>

<script setup lang="ts">
import RecentAnalysisTable from "~/components/common/RecentAnalysisTable.vue";

const auth = useAuth();
const { user, isAuthenticated } = useAuth();

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