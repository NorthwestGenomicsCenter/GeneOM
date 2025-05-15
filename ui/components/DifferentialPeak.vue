<template>
  <Header title="ChiaAtac Differential Peak" v-model="applyGlobals"/>
  <div>
    <DataTable
        :value="table"
        :paginator="true"
        :rows="10"
        :filters="filters"
        row-hover
        sort-field="ofInterest"
        :sort-order="1"
        @row-dblclick="rowClick">
      <template #groupheader="{data}">
        <div class="flex items-center gap-2">
          <span>{{ data?.ofInterest }}</span>
        </div>
      </template>
      <Column field="peakID" header="peakID" sortable frozen/>
      <Column field="start" header="start" sortable/>
      <Column field="stop" header="stop" sortable/>
      <Column field="fdr1" header="fdr1" sortable/>
      <Column field="fdr2" header="fdr2" sortable/>
      <Column field="log2fold1" header="log2fold1" sortable/>
      <Column field="log2fold2" header="log2fold2" sortable/>
      <Column field="overlappingGene" header="overlappingGene" sortable/>
      <Column field="finalCode" header="finalCode" sortable/>
      <Column field="fullCode" header="fullCode" sortable/>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import {useGlobalStore} from '@/composables/useGlobal'
import {FilterMatchMode, FilterOperator, FilterService} from "@primevue/core";
import Header from "~/components/common/Header.vue"

interface RnaSeqDifferentialPeak {
  peakId: string
  chrom: string
  start: string
  stop: string
  fdr1: string
  fdr2: string
  logFold1: string
  logFold2: string
  overlappingGene: string
  finalCode: string
  fullCode: string
}

defineOptions({
  name: 'RnaSeqDifferentialPeak',
})

const UNSELECTED_LABEL = "Unselected";

const table = ref<Array<RnaSeqDifferentialPeak>>([]);
const applyGlobals = ref<boolean>(false);
const store = useGlobalStore();

const filters = computed(() => {
  const geneFilterList = store.globalNav.genes
      .map(g => {
        return {
          value: g,
          matchMode: FilterMatchMode.CONTAINS
        }
      })

  return {
    global: { value: store.globalNav.gene, matchMode: FilterMatchMode.CONTAINS },
    overlappingGene: { operator: FilterOperator.OR, constraints: applyGlobals.value ? geneFilterList : [] },
  }
})

onMounted(async () => {
  table.value = await $fetch("/api/assay/rnaseq/differentialPeak/1");
  // table.value.forEach(row => row.selected = UNSELECTED_LABEL)
})

function rowClick(event) {
  let data = event.data;
  let gene = data.associated_gene;
  let store = useGlobalStore();
  store.addGene(gene)
}

</script>

<style scoped>
:deep(.centered>canvas) {
  margin-left: auto;
  margin-right: auto;
}


</style>