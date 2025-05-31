<template>
  <div class="samples bordered">
    <div class="header">
      <h2>Samples</h2>
    </div>
    <DataTable
        :value="samples">
      <Column header="Name" field="name" />
      <Column header="External Lims Id" field="external_lims_id" />
      <Column header="External Project Name" field="external_project_name" />
      <Column header="Description" field="description" />
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {FilterMatchMode} from "@primevue/core";

definePageMeta({
  layout: 'default'
})

const samples = ref([]);
const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS }
});

onMounted(() => {
  // Load initial data - replace with your API call
  loadSamples();
});

const loadSamples = async () => {
  samples.value = await $fetch("/api/sample/all");
};

</script>

<style scoped>
.samples {
  padding: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.table-header {
  display: flex;
  justify-content: flex-end;
}

.confirmation-content {
  display: flex;
  align-items: center;
}
</style>