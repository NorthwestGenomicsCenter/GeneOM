<template>
  <DataTable
      :value="resourceList"
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
        {{ data.name }}
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
  </DataTable>
  <section class="flex justify-end pt-8">
    <Button
      label="Cancel"
      icon="pi pi-times"
      severity="warning"
      outlined
      @click="visible = false"
    />
    <Button
      label="Apply"
      icon="pi pi-check"
      class="ml-1"
      @click="applyChanges"
    />
  </section>

</template>

<script setup lang="ts">
import {ref} from "vue";
import {FilterMatchMode, FilterOperator} from "@primevue/core";

const props = defineProps<{
  componentKey: string,
  filetypes?: Array<String>,
  visualizers?: String | Array<String>,
  tableData?: AnalysisResource[]}
>();

const emit = defineEmits<{
  selectResources: [AnalysisResource[]]
}>();

const visible = defineModel<boolean>("visible");

const resourceStore = useViewResourcesStore();
const {
  setViewResources,
  resourceList,
  selectedResources: f
} = resourceStore;
const selectedResources = ref<AnalysisResource[]>([] as AnalysisResource[]);

onMounted(() => {
  const preselected = f(props.componentKey)
  selectedResources.value = preselected.value
})

const applyChanges = () => {
  setViewResources(props.componentKey, selectedResources.value)
  visible.value = false;
}

// DataTable
const selectChange = () => {
  // emit("selectResources", selectedResources.value);
  // do nothing
}

/*******************************
 * Filters
 * ****************************/

const visualizerFilter = computed(() => {
  if (props.visualizers == null || props.visualizers.length == 0) return [{ value: null, matchMode: FilterMatchMode.ENDS_WITH }];
  let constraints = [];

  if (typeof props.visualizers == "string") {
    return [{ value: props.visualizers, matchMode: FilterMatchMode.CONTAINS }];
  }

  for (let visualizers of props.visualizers) {
    let constraint = { value: visualizers, matchMode: FilterMatchMode.CONTAINS }
    constraints.push(constraint);
  }

  return constraints;
});

const filetypeFilters = computed(() => {
  if (props.filetypes == null || props.filetypes.length == 0) return [{ value: null, matchMode: FilterMatchMode.ENDS_WITH }];
  let constraints = [];

  for (let filetype of props.filetypes) {
    let constraint = { value: filetype, matchMode: FilterMatchMode.CONTAINS }
    constraints.push(constraint);
  }

  return constraints;
});

const filterText = ref<string>(null);
const filters = {
  global: { value: filterText.value, matchMode: FilterMatchMode.CONTAINS },
  resourceName: {operator: FilterOperator.OR, constraints: filetypeFilters.value },
  technologiesViewers: {operator: FilterOperator.OR, constraints: visualizerFilter.value }
};

watch(filterText, (val) => filters.global.value = val);

</script>

<style scoped>

</style>