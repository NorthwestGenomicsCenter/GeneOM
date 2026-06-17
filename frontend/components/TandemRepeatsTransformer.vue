<template>
  <div class="de-container">
    <Header title="Tandem Repeats" disableToggle />
    <div class="p-input-icon-left mb-2">
      <i class="pi pi-search" />
      <InputText v-model="search" placeholder="Search..." class="w-full" />
    </div>
    <MultiSelect :modelValue="selectedColumns" :options="columns" @update:modelValue="onToggle"
                 display="chip" placeholder="Select Columns" />
    <DataTable
        :value="rawData"
        :rows="10"
        paginator
        stripedRows
        rowHover
        scrollable
        scrollHeight="600px"
        class="text-sm"
        :rowClass="() => 'table-row'"
        contextMenu
        v-model:contextMenuSelection="selectedRow"
        @rowContextmenu="onRowContextMenu"
    >
      <Column
          v-for="col in selectedColumns"
          :key="col"
          :field="col"
          :header="formatHeader(col)"
          headerClass="table-cell"
          class="table-cell"
          sortable>
        <template #body="{data, field}">
          <div class="table-cell">
            {{ data[field] }}
          </div>
        </template>
      </Column>
    </DataTable>
  </div>

  <ContextMenu ref="cm" :model="menuModel" @hide="selectedRow = null" />
  <Toast />
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import InputText from 'primevue/inputtext'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import {useGlobalStore} from "~/composables/useGlobal";
import Header from "~/components/common/Header.vue";

const props = defineProps<{
  omicResourceId: string
  sampleId: string
  technology: string
  viewName: string
  fileName: string
}>()

const route = useRoute();
const global = useGlobalStore();

const sampleId = computed(() => route.query.samples);

// const rawData = ref<Record<string, string>[]>()
// const response = await useApi(`/omic/visualize/${props.omicResourceId}`)
const { data: rawData } = await useApi<Record<string, string>[]>(`/omic/visualize/c896c7be-77ed-4e55-a8fb-9929c256dffc`)

const search = ref('')

const columns = ref([
  "priority_rank",  "flags",  "filters",  "chrom",  "start",  "end",  "variant_id",  "STRUC",
  "MOTIFS",  "PS",  "SD",  "MC",  "pathogenic_motifs",  "pathogenic_MC",  "pathogenic",  "normal_min",
  "normal_max",  "pathogenic_expansion_min",  "pathogenic_contraction_max",  "AL",  "ALLR",
  "expanded",  "pop_upper",  "pop_recessive_upper",  "gene",  "highest_consequence",  "highest_impact",
  "consequence",  "disease_gene",  "disease_id",  "disease_name",  "inheritance",  "notes"
])

const selectedColumns = ref([
  "priority_rank",  "flags",  "filters",  "chrom",
  "start",  "end",  "variant_id",  "pathogenic_motifs", "gene"
])

const onToggle = (val) => {
  selectedColumns.value = columns.value.filter(col => val.includes(col));
};

// context menu
const cm = ref();
const selectedRow = ref();

const menuModel = ref([
  {label: 'Navigate to in Igv', icon: 'pi pi-fw pi-search', command: () => navigateIgv(selectedRow)},
  {label: 'Graph', icon: 'pi pi-fw pi-search', command: () => navigateIgv(selectedRow)},
  {label: 'Save Position', icon: 'pi pi-fw pi-search', command: () => savePosition(selectedRow)},
  {label: 'Add Note', icon: 'pi pi-fw pi-search', command: () => addNote(selectedRow)},
]);

const onRowContextMenu = (event) => {
  cm.value.show(event.originalEvent);
};

const navigateIgv = (selectedRow) => {
  const row : TandemRepeatsDTO = selectedRow.value;
  const chrom = row.chrom;
  const start = row.start;
  const end = row.end;
  
  global.setCurrentGenomicPosition(chrom, start, end);
}

const savePosition = (selectedRow) => {
  const row : TandemRepeatsDTO = selectedRow.value;
  const chrom = row.chrom;
  const start = row.start;
  const end = row.end;

  global.changeGene(row.gene);
}

const addNote = (selectedRow) => {
  alert("Not yet implemented");
}


const filteredData = computed(() => {
  if (!search.value.trim()) return rawData.value
  const q = search.value.toLowerCase()
  return rawData.value.filter(row =>
      columns.some(col =>
          row[col]?.toLowerCase().includes(q)
      )
  )
})

const formatHeader = (header: string) =>
    header
        .replace(/_/g, ' ')
        .replace(/\b\w/g, c => c.toUpperCase())
</script>

<style scoped>
:deep(.table-row) {
  font-size: smaller;
  text-overflow: ellipsis;
}

:deep(.table-cell) {
  width: 25ch;
  text-overflow: ellipsis;
  text-wrap: nowrap;
}
</style>
