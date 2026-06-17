<template>
  <DataTable
      :value="files"
      :rows="10"
      :rowsPerPageOptions="[5,10,20]"
      :sort-order="1"
      :filters="filters"
      class="p-datatable-sm"
      dataKey="omicResourceId"
      rowHover
      paginator
      removableSort
      sort-field="lastName"
      stripedRows
      v-model:editingRows="editingRows"
      @rowDblclick="event => editingRows = [...editingRows, event.data]"
      @rowEditSave="onRowEditSave"
      editMode="row"
  >
    <template #header>
      <div class="table-header">
        <IconField>
          <InputIcon class="pi pi-search" />
          <InputText v-model="filters.global.value" placeholder="Search files..." />
        </IconField>
        <div>
          <slot name="header-right" />
          <Button :label="showAll ? 'Show Visualizable' : 'Show All'" @click="show()" />
        </div>
      </div>
    </template>

    <Column field="path" header="Path" sortable></Column>
    <Column field="displayName" header="Display Name" sortable>
      <template #editor="{data} : {data : OmicResourceDTO}">
        <InputText size="small" v-model="data.displayName"/>
      </template>
    </Column>
    <Column field="description" header="Description" sortable>
      <template #editor="{data} : {data : OmicResourceDTO}">
        <InputText size="small" v-model="data.description"/>
      </template>
    </Column>
    <Column field="tissue" header="Tissue" sortable>
      <template #body="{data}">
        {{ data.tissue['url'] }}
      </template>
      <template #editor="{data}">
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
      <template #editor="{data}">
        <MultiSelect
          v-model="data.visualizationComponents"
          placeholder="Visualisation Components"
          display="chip"
          :options="visualizerOptions"
          option-label="label"
          option-value="value"
        />
      </template>
    </Column>
    <Column field="integrationSource" header="Lims Source" sortable></Column>
    <Column :rowEditor="true" style="width: 10%; min-width: 8rem" bodyStyle="text-align:center"></Column>
    <template #footer>
      Files: {{ props.files?.length }}
    </template>
  </DataTable>
</template>

<script setup lang="ts">
import TissueDropdown from "~/components/common/TissueDropdown.vue";
import type {DataTableRowEditSaveEvent} from "primevue";
import {FilterMatchMode} from "@primevue/core";

const toast = useToast();

defineOptions({
  name: "SampleFilesTable"
});

const props = defineProps<{
  files: any[],
  refreshTableData: Function
}>();

const filterText = ref<string>();
const filters = ref({
  global: { value: filterText.value, matchMode: FilterMatchMode.CONTAINS },
  visualizationComponents: { value: [], matchMode: FilterMatchMode.NOT_EQUALS}
});
const showAll = ref<boolean>(false);
const show = () => {
  showAll.value = !showAll.value;
  if (showAll.value) {
    delete filters.value.visualizationComponents
  } else {
    filters.value['visualizationComponents'] = { value: [], matchMode: FilterMatchMode.NOT_EQUALS}
  }
}

const visualizerOptions = useApi("/omic/visualizer-options").data;

const editingRows = ref();
const onRowEditSave = async (row : DataTableRowEditSaveEvent) => {
  const oldData : OmicResourceDTO = row.data;
  const newData : OmicResourceDTO = row.newData;

  const resourceId = oldData.omicResourceId;

  if (!areEqualShallow(oldData, newData)) {
    const body : EditOmicResourceDTO = newData;

    try {
      const res = await useClientApi(`/omic/edit/${resourceId}`, {
        method: "POST",
        body: body
      })
      toast.add({severity: "info", summary: "Saved", life: 1000});

      props.refreshTableData();
    } catch (error) {
      toast.add({
        severity: "error",
        summary: "Error",
        detail: error,
        life: 8000
      })
    }
  }
}

function areEqualShallow(a, b) {
  for (let key in a) {
    if (!(key in b) || a[key] !== b[key]) {
      return false;
    }
  }
  for (let key in b) {
    if (!(key in a)) {
      return false;
    }
  }
  return true;
}

const tissueChange = () => {
  alert("Change")
}
</script>

<style scoped>
.table-header {
  display: flex;
  justify-content: space-between
}
</style>