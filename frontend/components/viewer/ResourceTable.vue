<template>
  <DataTable
      :value="tableData"
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
      <InputText placeholder="Search..." />
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
  {{ igvTracks }}
</template>

<script setup lang="ts">
import {useSampleViewStore} from "~/composables/useSampleViewStore";

const viewStore = useSampleViewStore();
const {
  sampleId,
  techViewOptions,
  selectedViews,
  layout,
  components,
  igvTracks,
  selectedResources
} = storeToRefs(viewStore);
const { addView, removeView, updateLayout, setSampleId, addAnalysisResource } = viewStore;

const props = defineProps<{tableData: AnalysisResource[]}>();

// DataTable
const selectChange = (event, old) => {
  addAnalysisResource(event)
}

</script>

<style scoped>

</style>