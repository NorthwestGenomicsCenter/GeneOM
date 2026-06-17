<template>
  <DataTable
      :value="analyses"
      rowHover
      :paginator="true"
      :rows="10"
      @rowClick="navAssay"
  >
    <Column field="name" header="Analysis Name"/>
    <Column field="id" header="Run Id"/>
    <Column field="lastAccessedTime" header="Last Time Accessed"/>
  </DataTable>
</template>

<script setup lang="ts">
defineProps({
  analyses: {},
})

const {data: analyses, refresh, error} = await useApi<AvailableUserAnalysisDTO[]>('/analysis/available');

const router = useRouter();
const navAssay = async (event) => {
  const id = event.data.id;
  await router.push({path: `/analysis/view-${id}`})
}

</script>

<style scoped>

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