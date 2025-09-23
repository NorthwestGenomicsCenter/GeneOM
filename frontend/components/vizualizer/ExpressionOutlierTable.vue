<template>
  <Header title="Expression Outlier Table" disableToggle>
    <template #left>
      <em class="pi pi-cog" @click="selectDialogVisible = true"/>
    </template>
    <template #right>
      <em class="pi pi-times-circle" @click="removeView(componentKey)"/>
    </template>
  </Header>
  <div>
    <DataTable
      :value="table"
      :paginator="true"
      :rows="10"
      :filters="filters"
      row-hover
      @row-dblclick="rowClick" >
      <Column field="id" header="ID" sortable />
      <Column field="Isoform_PBid" header="Isoform PBid" sortable />
      <Column field="associated_gene" header="Associated Gene" sortable></Column>
      <Column field="Sample" header="Sample" sortable></Column>
      <Column field="Cyclo_TPM" header="Cyclo TPM" sortable></Column>
      <Column field="Noncyclo_TPM" header="Noncyclo TPM" sortable></Column>
      <Column field="test_statistic" header="Test Statistic" sortable></Column>
      <Column field="z_score" header="Z-Score" sortable></Column>
      <Column field="NormalizedFractionDifference" header="Normalized Fraction Difference" sortable></Column>
      <Column field="OMIM_phenotypes" header="OMIM Phenotypes"></Column>
      <Column field="rank_top_95_percentile" header="Rank (Top 95 Percentile)" sortable></Column>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import Chart from 'primevue/chart'
import { useGlobalStore } from '@/composables/useGlobal'
import {FilterMatchMode, FilterService} from "@primevue/core";
import { mapStores } from 'pinia'
import listContains from '@/utils/custom_filters'
import Header from "~/components/common/Header.vue";
import {useSampleViewStore} from "~/composables/useSampleViewStore";

interface ExpressionOutlier {
  id: number
  Isoform_PBid: string
  associated_gene: string
  Sample: string
  Cyclo_TPM: number
  Noncyclo_TPM: number
  test_statistic: number
  z_score: number
  NormalizedFractionDifference: number
  OMIM_phenotypes: string
  rank_top_95_percentile: number
}

const selectDialogVisible = ref<boolean>(false);
const props = defineProps<{
  componentKey: any
}>();

const viewStore = useSampleViewStore();
const { removeView } = viewStore;

const table = ref();
const res = await useApi<ExpressionOutlier>("/assay/isoform/cutoff/99", {
  method: "GET"
})
table.value = res.data;

const rowClick = (event) => {
  let data = event.data;
  let gene = data.associated_gene;
  let store = useGlobalStore();
  store.addGene(gene)
}
const select = (e) => {
  console.log(e)
}

defineOptions({
  name: 'ExpressionOutlier',
})
</script>

<style scoped>
:deep(.centered>canvas) {
  margin-left: auto;
  margin-right: auto;
}


</style>