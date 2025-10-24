<template>
  <div class="samples bordered">
    <div class="header">
      <h2>Samples</h2>
      <Button
          icon="pi pi-external-link"
          label="Import"
          severity="info"
          outlined
          raised
          @click="importSamples" />
    </div>
    <DataTable
        :value="samples">
      <Column header="Name" field="name" />
      <Column header="External Lims Id" field="externalLimsId" />
      <Column header="External Project Name" field="externalProjectName" />
      <Column header="Description" field="description" />
      <Column header="Owners" field="users">
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

</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {FilterMatchMode} from "@primevue/core";
import SampleChip from '~/components/samples/SampleChip.vue';
import ImportExternalData from "~/pages/import/ImportExternalData.vue";

definePageMeta({
  layout: 'default'
})

const { data: samples, refresh } = await useApi<SampleDTO[]>("/sample/all");
const { data: testData } = await useApi("/sample/test");

const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS }
});


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