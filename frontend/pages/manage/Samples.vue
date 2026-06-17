<template>
  <div class="samples bordered">
    <div class="header">
      <h2>Samples</h2>
      <div>
        <Button
            icon="pi pi-external-link"
            label="Create Sample"
            severity="secondary"
            outlined
            raised
            @click="createSample" />
        <Button
            icon="pi pi-external-link"
            label="Import"
            severity="info"
            outlined
            raised
            @click="importSamples" />
      </div>
    </div>
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
    >
      <Column header="Name" field="name" sortable />
      <Column header="External Lims Id" field="externalLimsId" sortable />
      <Column header="External Project Name" field="externalProjectName" :showFilterMatchModes="false" sortable>
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
      <Column header="Description" field="description" sortable >
        <template #body="{data}">
          <CommonTruncate :text="data.description" :stop="30" />
        </template>
      </Column>
      <Column header="Owners" field="users" sortable >
        <template #body="{data}">
          <Tag v-for="user in data.users">
            {{ fullName(user) }}
          </Tag>
        </template>
      </Column>
      <Column header="edit" >
        <template #body="{data}">
          <NuxtLink :to="`/manage/samples/edit/${data.id}`">Edit</NuxtLink>
        </template>
      </Column>
    </DataTable>
  </div>

<!--  <SampleChip :sample="samples[0]" />-->
  <Dialog
      header="Import From External System"
      v-model:visible="importDialogVisible"
      modal
  >
    <ImportExternalData @update="importUpdated" />
  </Dialog>

  <Dialog
      header="Create Sample"
      v-model:visible="createSampleDialogVisible"
      modal
  >
    <CreateNewSample @close="createSampleDialogVisible = false; refresh()" />
  </Dialog>

</template>

<script setup lang="ts">
import {ref} from 'vue';
import {FilterMatchMode} from "@primevue/core";
import SampleChip from '~/components/samples/SampleChip.vue';
import ImportExternalData from "~/pages/import/ImportExternalData.vue";
import CreateNewSample from "~/components/samples/CreateNewSample.vue";

definePageMeta({
  layout: 'default'
})

const { data: samples, refresh } = await useApi<SampleDTO[]>("/sample/all");

const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  externalProjectName: { value: null, matchMode: FilterMatchMode.EQUALS }
});

const uniqueProjectNames = computed(() =>
    [...new Set(samples.value.map(r => r.externalProjectName).filter(Boolean))].sort((a, b) => a.localeCompare(b))
)

const editVisible = ref<boolean>(false);
const sampleToEdit = ref<any>({});
const editSample = (row) => {
  sampleToEdit.value = row;
  editVisible.value = true;
}

const importDialogVisible = ref<boolean>(false);
const importSamples = () => importDialogVisible.value = true;

const importUpdated = () => {
  importDialogVisible.value = false;
  refresh();
}

const createSampleDialogVisible = ref<boolean>(false);
const createSample = () => {
  createSampleDialogVisible.value = true;
}

const fullName = (user : UserDTO) => {
  return `${user.firstName} ${user.lastName}`;
}
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