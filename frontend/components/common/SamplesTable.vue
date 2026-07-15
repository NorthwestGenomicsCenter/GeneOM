<template>
  <DataTable
      :value="samples"
      stripedRows
      rowHover
      scrollable scrollHeight="400px" :virtualScrollerOptions="{ itemSize: 44 }"
      sort-field="name"
      :sort-order="1"
      :rows="10"
      dataKey="id"
      v-model:filters="filters"
      filterDisplay="menu"
      v-bind="$attrs"
  >
    <slot name="left" />

    <Column header="Name" field="name" :sortable="true" >
      <template #body="{data}">
          <span style="width: 22ch; display: block;">
            {{data.name}}
          </span>
      </template>
    </Column>
    <Column header="External Lims Id" field="externalLimsId" :sortable="true" />
    <Column header="External Project Name" field="externalProjectName" :showFilterMatchModes="false" :sortable="true">
      <template #body="{data}">
          <span style="width: 54ch; display: block;">
            {{data.externalProjectName}}
          </span>
      </template>
      <template #filter="{ filterModel, filterCallback }">
        <h2>Project Name</h2>
        <Dropdown
            v-model="filterModel.value"
            :options="uniqueProjectNames"
            placeholder="All"
            showClear
            class="w-full"
        />
      </template>
    </Column>
    <Column header="Description" field="description" :sortable="true" >
      <template #body="{data}">
          <span style="width: 40ch; display: block;">
            <CommonTruncate :text="data.description" :stop="30" />
          </span>
      </template>
    </Column>
    <Column header="Owners" field="users" :sortable="true">
      <template #body="{data}">
        <Tag v-for="user in data.users">
          {{ fullName(user) }}
        </Tag>
      </template>
    </Column>
    <slot/>
  </DataTable>
</template>

<script setup lang="ts">
import {ref} from 'vue';
import {FilterMatchMode} from "@primevue/core";
const props = defineProps<{
  samples: SampleDTO[];
}>()

const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  externalProjectName: { value: null, matchMode: FilterMatchMode.EQUALS }
});

const uniqueProjectNames = computed(() =>
    [...new Set(props.samples.map(r => r.externalProjectName).filter(Boolean))].sort((a, b) => a.localeCompare(b))
)

const fullName = (user : UserDTO) => {
  return `${user.firstName} ${user.lastName}`;
}
</script>

<style scoped>

</style>