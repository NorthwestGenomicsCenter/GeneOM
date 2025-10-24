<template>
  <DataTable
      :value="analysis.resources"
      :filters="filters"
      v-model:selection="selectedResources"
      selectionMode="multiple"
      rowGroupMode="rowspan"
      groupRowsBy="sampleId"
      sortMode="single"
      sortField="sampleId"
      :sortOrder="1"
      dataKey="resourceId"
      paginator
      :rows="25"
      :rowsPerPageOptions="[5, 10, 15, 20, 25, 30, 35, 40, 45, 50]"
      @update:selection="selectChange">
    <template #header>
      <InputText placeholder="Search..." v-model="filterText" />
    </template>

    <Column header="Sample" field="sampleId">
      <template #body="{ data } : { data: AnalysisSample }">
        {{ data.sampleName }}
      </template>
    </Column>
    <Column selectionMode="multiple" headerStyle="width: 3rem"></Column>
    <Column header="FileName" field="resourceName" />
    <Column header="Sample Name" field="sampleName" />
    <Column header="Viewers">
      <template #body="{ data } : { data: AnalysisResource }">
        <Tag
            v-for="viewer in data.technologiesViewers"
            severity="secondary">
          {{ viewer }}
        </Tag>
      </template>
    </Column>
    <!--      <Column header="Omic Data">-->
    <!--        <template #body="{ data } : { data: AnalysisResource }">-->
    <!--          {{ data }}-->
    <!--        </template>-->
    <!--      </Column>-->
  </DataTable>
</template>

<script setup lang="ts">
import {ref} from "vue";
import { FilterMatchMode } from "@primevue/core";
import {useSampleViewStore} from "~/composables/useSampleViewStore";


const props = defineProps<{
  filetypes: Array<String>,
  tableData: AnalysisResource[]}
>();

const emit = defineEmits<{
  selectResources: [AnalysisResource[]]
}>();

const selectedResources = ref<AnalysisResource[]>([] as AnalysisResource[]);

const viewStore = useSampleViewStore();
const {
  analysis,
} = storeToRefs(viewStore);
const { refreshAnalysis } = viewStore;

onMounted(async () => await refreshAnalysis())

const filterText = ref<string>(null);
const filters = {
  global: { value: filterText.value, matchMode: FilterMatchMode.CONTAINS }
};
watch(filterText, (val) => filters.global.value = val);

// DataTable
const selectChange = () => {
  emit("selectResources", selectedResources.value);
}

</script>

<style scoped>

</style>