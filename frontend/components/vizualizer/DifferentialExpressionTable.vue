<template>
  <div class="de-container">
    <Header title="Differential Expression" v-model="applyGlobals">
      <template #left>
        <em class="pi pi-cog" @click="selectDialogVisible = true"/>
      </template>
      <template #right>
        <em class="pi pi-times-circle" @click="removeView(componentKey)"/>
      </template>
    </Header>
    <div class="table-wrapper">
      <DataTable
          :value="table"
          :paginator="true"
          :rows="10"
          :filters="filters"
          row-hover
          filterDisplay="menu"
          @row-dblclick="rowClick"
          v-model:contextMenuSelection="selectedRow"
          @rowContextmenu="onRowContextMenu">
<!--        <template #header>-->
<!--          <InputText v-model="searchText" placeholder="Search..."/>-->
<!--        </template>-->
        <Column field="gene_name" header="Gene" sortable></Column>
        <Column field="logFC" header="logFC" sortable>
          <template #body="{ data }">
            {{ data.logFC }}
          </template>
          <template #filter="{ filterModel }">
            <InputText v-model="filterModel.value" type="text" placeholder="Search by name" />
          </template>
        </Column>
        <Column field="logCPM" header="logCPM" sortable></Column>
        <Column field="F" header="F" sortable></Column>
        <Column field="PValue" header="PValue" sortable></Column>
        <Column field="FDR" header="FDR" sortable></Column>
        <template #footer>

        </template>
      </DataTable>
    </div>

    <GeneSelectPopover :genes="genesList" ref="op" />
    <div class="popover-anchor" style="position: absolute" ref="anchor"></div>
  </div>

  <ContextMenu ref="cm" :model="menuModel" @hide="selectedRow = null" />
</template>

<script setup lang="ts">
import Chart from 'primevue/chart'
import Header from "~/components/common/Header.vue"
import GeneSelectPopover from "~/components/common/GeneSelectPopover.vue"

import {useGlobalStore} from '@/composables/useGlobal'
import {FilterMatchMode, FilterOperator} from "@primevue/core";
import {useSampleViewStore} from "~/composables/useSampleViewStore";
import DataTable from "primevue/datatable";

const op = ref();
const anchor = ref();
const genesList = ref<string[]>([]);
const global = useGlobalStore();

// context menu
const cm = ref();
const selectedRow = ref();

const onRowContextMenu = (event) => {
  cm.value.show(event.originalEvent);
};

const menuModel = ref([
  {label: 'Navigate to in Igv', icon: 'pi pi-fw pi-search', command: () => navigateIgv(selectedRow)},
  {label: 'Graph', icon: 'pi pi-fw pi-search', command: () => navigateIgv(selectedRow)},
  {label: 'Save Position', icon: 'pi pi-fw pi-search', command: () => navigateIgv(selectedRow)},
  {label: 'Add Note', icon: 'pi pi-fw pi-search', command: () => navigateIgv(selectedRow)},
]);

const navigateIgv = (selectedRow) => {
  const row = selectedRow.value;
  const chrom = row.seqnames;
  const start = row.start;
  const end = row.end;

  global.setCurrentGenomicPosition(chrom, start, end);
}


interface DifferentialExpression {
  transcript: string
  logFC: Number
  logCPM: Number
  F: Number
  PValue: Number
  FDR: Number
  geneName: string
}

const selectDialogVisible = ref<boolean>(false);
const props = defineProps<{
  componentKey: any
}>();

const viewStore = useSampleViewStore();
const { removeView } = viewStore;

defineOptions({
  name: 'DifferentialExpressionTable'
})

const applyGlobals = ref<boolean>(false);
const chartRawData = ref<any>();
const chartOptions = ref<any>();
const table = ref<Array<DifferentialExpression>>([]);
const differentialExpressionChart = ref<any>();
const filterLimits = ref<any>({
  "fdr1": {
    "min": 1.15e-89,
    "max": 0.049999969
  },
  "fdr2": {
    "min": 3.11e-60,
    "max": 0.049973044
  },
  "log2fold1": {
    "min": -8.81,
    "max": 9.21
  },
  "log2fold2": {
    "min": -9.89,
    "max": 8.95
  }
});

onMounted(async () => {
  table.value = await $fetch("/api/assay/differential_expression/table/1");
  filterLimits.value = await $fetch("/api/assay/differentialPeak/MATTRESS15/1");
});


const store = useGlobalStore();

const searchText = ref<string>("NL");
const filters = computed(() => {
  const geneFilterList = store.globalNav.genes
      .map(g => {
        return {
          value: g,
          matchMode: FilterMatchMode.EQUALS
        }
  })

  // return {
  //   global: { value: store.globalNav.gene, matchMode: FilterMatchMode.CONTAINS },
  //   geneName: { operator: FilterOperator.OR, constraints: applyGlobals.value ? geneFilterList : [] },
  // }

  return {
    global: { value: searchText.value, matchMode: FilterMatchMode.CONTAINS },
  }
})

function rowClick(event) {
  let data = event.data;
  let gene = data.gene_name;
  let store = useGlobalStore();
  store.addGene(gene)
}

function select(e) {
  console.log(e)
}

const backgroundColors = [
  "#999999", // Neutral Grey
  "#d5ac4d", // Red (Hot) - actually orange for accessibility
  "#56B4E9"  // Blue (Cold)
];

const highlightColors = [
  "#0072B2", // Deep Blue
  "#D55E00", // Vermillion/Orange
  "#009E73", // Bluish Green
  "#F0E442", // Yellow
  "#CC79A7", // Pink/Magenta
  "#56B4E9", // Sky Blue
  "#E69F00", // Gold/Orange
  "#000000", // Black (high contrast)
  "#654321", // Brown (distinct)
  "#785EF0"  // Violet/Purple
];

const getGroupColor = (groupName) => {
  return groupName.toLowerCase() === 'unlabeled' ? backgroundColors[0] :
      `hsl(${Math.floor(Math.random() * 360)}, 70%, 60%)`;
};


</script>

<style scoped>
:deep(.centered>canvas) {
  margin-left: auto;
  margin-right: auto;
}

:deep(.chart) {
  width: 100%;
  height: 100%;
  position: relative;
}

:deep(.chart > canvas) {
  width: 100% !important;
  height: 100% !important;
}

.de-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 500px; /* ⬅️ This is your minimum height */
  overflow: hidden;
}

.chart-wrapper {
  flex: 1 1 60%;
  min-height: 200px;
  overflow: hidden;
}

.table-wrapper {
  flex: 1 1 40%;
  min-height: 200px;
  overflow: auto;
}

</style>