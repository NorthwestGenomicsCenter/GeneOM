<template>
  <div>
    <div class="de-container">
      <Header
          title="Differential Expression Table"
          v-model="applyGlobals"
          @settings="selectDialogVisible = true"
          @close="removeView(componentKey)"
      >
      </Header>

      <div class="table-wrapper">
        <DataTable
            :value="table"
            :paginator="true"
            :rows="10"
            v-model:filters="filters"
            v-model:contextMenuSelection="selectedRow"
            row-hover
            filterDisplay="menu"
            @filter="onFilter"
            @row-dblclick="rowClick"
            @rowContextmenu="onRowContextMenu">
          <template #header>
            <div class="flex items-center justify-between">
              <InputText v-model="searchText" placeholder="Search..."/>
              <Tag v-if="loadedFiles">{{ loadedFiles }}</Tag>
            </div>
          </template>
          <template #empty>
            <h3>No Data loaded for Table. Click Gear to Select Data File</h3>
          </template>
          <Column field="gene_name" header="Gene" :showFilterMatchModes="false" sortable>
            <template #filter="{ filterModel }">
              <MultiSelect
                  v-model="filterModel.value"
                  filter
                  :options="genesList"
                  :virtualScrollerOptions="{ itemSize: 44 }"
                  placeholder="Any"/>
            </template>
          </Column>

          <Column field="seqnames" header="Chrom." :showFilterMatchModes="false" sortable>
            <template #filter="{ filterModel }">
              <MultiSelect
                  v-model="filterModel.value"
                  filter
                  :options="chromosomes"
                  placeholder="Any"/>
            </template>
          </Column>

          <Column field="logFC" header="logFC" :showFilterMatchModes="false" sortable>
            <template #body="{ data }">
              {{ data.logFC }}
            </template>
            <template #filter="{ filterModel }">
              <CommonSliderFilter v-model="filterModel.value" :data="table" field="logFC"/>
            </template>
          </Column>
          <Column field="logCPM" header="logCPM" :showFilterMatchModes="false" sortable>
            <template #filter="{ filterModel }">
              <CommonSliderFilter v-model="filterModel.value" :data="table" field="logCPM"/>
            </template>
          </Column>
          <Column field="F" header="F" :showFilterMatchModes="false" sortable>
            <template #filter="{ filterModel }">
              <CommonSliderFilter v-model="filterModel.value" :data="table" field="F"/>
            </template>
          </Column>
          <Column field="PValue" header="PValue" :showFilterMatchModes="false" sortable>
            <template #filter="{ filterModel }">
              <CommonSliderFilter v-model="filterModel.value" :data="table" field="PValue"/>
            </template>
          </Column>
          <Column field="FDR" header="FDR" :showFilterMatchModes="false" sortable>
            <template #filter="{ filterModel }">
              <CommonSliderFilter v-model="filterModel.value" :data="table" field="FDR"/>
            </template>
          </Column>
          <template #footer>

          </template>
        </DataTable>
      </div>
    </div>

    <ContextMenu ref="cm" :model="menuModel" @hide="selectedRow = null"/>

    <Dialog header="Select Samples" v-model:visible="selectDialogVisible" modal>
      <ViewerResourceTable
          :componentKey="componentKey"
          :visualizers="['Differential Expression']"
          v-model:visible="selectDialogVisible"
      />
    </Dialog>
  </div>

</template>

<script setup lang="ts">
import DataTable from "primevue/datatable";
import Header from "~/components/common/Header.vue"
import {FilterMatchMode} from "@primevue/core";
import {useGlobalStore} from '@/composables/useGlobal'
import {useSampleViewStore} from "~/composables/useSampleViewStore";

const global = useGlobalStore();

const toast = useToast();

const viewStore = useSampleViewStore();
const {removeView} = viewStore;

const selectDialogVisible = ref<boolean>(false);
const props = defineProps<{
  componentKey: any
}>();

const resourcesStore = useViewResourcesStore();
const {selectedResources} = resourcesStore;
const resources = selectedResources(props.componentKey);

const table = ref([]);
const updateResourceData = async () => {
  table.value = [];
  if (resources.value.length == 0) return

  try {
    table.value = await useClientApi(`/omic/visualize/transform/DiffExpressionTableVisualizer/${resources.value[0].resourceId}`);
  } catch (error) {
    toast.add({
      severity: "error",
      summary: "Unable to Retrieve Resource",
      detail: `Trying to find resource for Differential Expression Table. \n\n ${error}`,
      life: 10_000
    })
  }
}

watch(resources, updateResourceData, {deep: true, immediate: true})

const loadedFiles = computed(() => {
  if (resources.value.length == 0) return null;

  let fileNames = "";
  for (let name of resources.value) {
    fileNames += name.resourceName + " ";
  }

  return fileNames;
})

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

  const position: GenomicPosition = {
    chr: row.seqnames,
    start: row.start,
    end: row.end
  };

  global.navigate(position);
}

defineOptions({
  name: 'DifferentialExpressionTable'
})

const applyGlobals = ref<boolean>(false);
const store = useGlobalStore();

const searchText = ref<string>();

const filteredTableValues = ref([]);

function onFilter(event) {
  // event.filteredValue contains only rows that passed filters
  filteredTableValues.value = event.filteredValue;
}

const chromosomes = computed(() => uniqueSortedArray(table.value, 'seqnames'));
const genesList = computed(() => uniqueSortedArray(table.value, 'gene_name'));

const uniqueSortedArray = (arr: any[], field: string) => {
  const set = new Set();
  arr.forEach((el) => {
    set.add(el[field])
  })

  return set.values()
      .toArray()
      .sort();
}

const filters = ref();
const initFilters = () => {
  filters.value = {
    global: {value: null, matchMode: FilterMatchMode.CONTAINS},

    gene_name: {value: null, matchMode: FilterMatchMode.IN},
    seqnames: {value: null, matchMode: FilterMatchMode.IN},
    logFC: {value: null, matchMode: FilterMatchMode.BETWEEN},
    logCPM: {value: null, matchMode: FilterMatchMode.BETWEEN},
    F: {value: null, matchMode: FilterMatchMode.BETWEEN},
    PValue: {value: null, matchMode: FilterMatchMode.BETWEEN},
    FDR: {value: null, matchMode: FilterMatchMode.BETWEEN}
  };
}
initFilters();

function rowClick(event) {
  let data = event.data;
  let gene = data.gene_name;
  let store = useGlobalStore();
  store.addGene(gene)
}

</script>

<style scoped>
.de-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 500px; /* ⬅️ This is your minimum height */
  overflow: hidden;
}

.table-wrapper {
  flex: 1 1 40%;
  min-height: 200px;
  overflow: auto;
}

</style>