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
      <Column header="Owners" field="users">
        <template #body="{data}">
          <Tag v-for="user in data.users">
            {{ fullName(user) }}
          </Tag>
        </template>
      </Column>
      <Column header="edit" >
        <template #body="{data}">
          <Button label="Edit" size="small" @click="editSample(data)" />
        </template>
      </Column>
    </DataTable>
  </div>

  <SampleChip :sample="samples[0]" />

  <Dialog header="Edit Sample" v-model:visible="editVisible" @after-hide="loadSamples">
    <EditSample
      v-model:visible="editVisible"
      v-model:sample="sampleToEdit" />
  </Dialog>

</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {FilterMatchMode} from "@primevue/core";
import EditSample from '~/components/samples/EditSample.vue';
import SampleChip from '~/components/samples/SampleChip.vue';

definePageMeta({
  layout: 'default'
})

const samples = ref([]);
const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS }
});

const loadSamples = async () => {
  const { data } = await useFetch("/api/sample/all");
  console.table(data)
  samples.value = data.value;
};

await loadSamples();

const editVisible = ref<boolean>(false);
const sampleToEdit = ref<any>({});
const editSample = (row) => {
  sampleToEdit.value = row;
  editVisible.value = true;
}

const fullName = (user) => {
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