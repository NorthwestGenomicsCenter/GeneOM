<template>
  <DataTable
    :value="samples"
    :selectAll="false"
    v-model:selection="selected"
    @rowSelect="rowSelect"
    @rowUnselect="rowSelect"
    >

    <Column selectionMode="multiple" headerStyle="width: 3rem"></Column>
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
  </DataTable>
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
const selected = ref<SampleDTO[]>([]);

const loadSamples = async () => {
  const { data } = await useApi("/sample/all");
  console.table(data)
  samples.value = data.value;
};
await loadSamples();

const emit = defineEmits<{
  update: [selected: SampleDTO[]]
}>()

const rowSelect = () => {
  emit("update", selected.value );
}

const fullName = (user : UserDTO) => {
  return `${user.firstName} ${user.lastName}`;
}
</script>

<style scoped>

</style>