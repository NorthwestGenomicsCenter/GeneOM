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
          :filters="filters"
          :rows="10"
          v-model:contextMenuSelection="selectedRow"
          row-hover
          filterDisplay="menu"
          @row-dblclick="rowClick"
          @rowContextmenu="onRowContextMenu">
        <template #header>
          <div class="flex items-center justify-between">
            <InputText v-model="searchText" placeholder="Search..."/>
            <Tag v-if="loadedFiles">{{loadedFiles}}</Tag>
          </div>
        </template>
        <template #empty>
          <h3>No Data loaded for Table. Click Gear to Select Data File</h3>
        </template>
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

  <Dialog header="Select Samples" v-model:visible="selectDialogVisible" modal>
    <ViewerResourceTable
        :componentKey="componentKey"
        :visualizers="['Differential Expression']"
        v-model:visible="selectDialogVisible"
    />
  </Dialog>

</template>

<script setup lang="ts">
import Header from "~/components/common/Header.vue"
import GeneSelectPopover from "~/components/common/GeneSelectPopover.vue"

import {useGlobalStore} from '@/composables/useGlobal'
import {FilterMatchMode} from "@primevue/core";
import {useSampleViewStore} from "~/composables/useSampleViewStore";
import DataTable from "primevue/datatable";

const op = ref();
const anchor = ref();
const genesList = ref<string[]>([]);
const global = useGlobalStore();

const toast = useToast();

const viewStore = useSampleViewStore();
const { layout, customReference, analysis } = storeToRefs(viewStore);
const { removeView } = viewStore;

const selectDialogVisible = ref<boolean>(false);
const props = defineProps<{
  componentKey: any
}>();

const resourcesStore = useViewResourcesStore();
const {setViewResources, selectedResources, state} = resourcesStore;

const resources = selectedResources(props.componentKey);

const table = ref();
const updateResourceData = async () => {
  table.value = [];
  if (resources.value.length == 0) return

  try {
    table.value = await useClientApi(`/omic/visualize/transform/DiffExpressionVisualizer/${resources.value[0].resourceId}`);
  } catch (error) {
    toast.add({severity: "error", summary: "Unable to Retrieve Resource", detail: `Trying to find resource for Differential Expression Table. \n\n ${error}`, life: 10_000})
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

  const position : GenomicPosition = {
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
const filters = computed(() => {
  const geneFilterList = store.globalNav.genes
      .map(g => {
        return {
          value: g,
          matchMode: FilterMatchMode.EQUALS
        }
  })
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