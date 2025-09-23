<template>
  <DataTable
      :value="files"
      :rows="10"
      :rowsPerPageOptions="[5,10,20]"
      :sort-order="1"
      class="p-datatable-sm"
      dataKey="id"
      paginator
      removableSort
      sort-field="lastName"
      stripedRows
  >
    <template #header>
      <div class="table-header">
          <span class="p-input-icon-left">
            <i class="pi pi-search" />
<!--            <InputText v-model="filters.global.value" placeholder="Search users..." />-->
          </span>
      </div>
    </template>

    <Column field="path" header="Path" sortable></Column>
    <Column field="tissue" header="Tissue" sortable>
      <template #body="{data}">
        {{ data.tissue['url'] }}
      </template>
      <template #edit="{data}">
        <TissueDropdown
            id="tissue-dropdown"
            v-model="data.tissue['url']"
            @change="tissueChange" />
      </template>
    </Column>
    <Column field="visualizationComponents" header="Vizualizers" sortable>
      <template #body="{data}">
        <Tag v-for="viz in data.visualizationComponents">{{viz}}</Tag>
      </template>
    </Column>
    <Column field="integrationSource" header="Lims Source" sortable></Column>
  </DataTable>
</template>

<script setup lang="ts">

import TissueDropdown from "~/components/common/TissueDropdown.vue";

const props = defineProps<{
  files: any
}>();

const tissueChange = () => {
  alert("Change")
}
</script>

<style scoped>

</style>