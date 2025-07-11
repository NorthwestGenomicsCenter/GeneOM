<template>
  <div class="samples bordered">
    <div class="header">
      <h2>Samples</h2>
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
          <NuxtLink :to="`/samples/edit/${data.id}`">Edit</NuxtLink>
        </template>
      </Column>
    </DataTable>
  </div>
  {{ testData }}

<!--  <SampleChip :sample="samples[0]" />-->

</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {FilterMatchMode} from "@primevue/core";
import SampleChip from '~/components/samples/SampleChip.vue';

definePageMeta({
  layout: 'default'
})

const testData = ref();
const getTestData = async () => {
  const { data } = await useApi("/sample/test");
  testData.value = data;
}

await getTestData();

const samples = ref<SampleDTO[]>([]);
const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS }
});

const loadSamples = async () => {
  const { data } = await useApi("/sample/all");
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