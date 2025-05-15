+<template>
  <h3>Expression Outliers</h3>
  <div>
    <Chart
        type="scatter"
        :data="chartData"
        :options="chartOptions"
        :width="1000"
        :height="400"
        @select="select"
        class="chart centered"
        ref="expressionOutlierChart"
      />
  </div>
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

<script lang="ts">
import Chart from 'primevue/chart'
import { useGlobalStore } from '@/composables/useGlobal'
import {FilterMatchMode, FilterService} from "@primevue/core";
import { mapStores } from 'pinia'
import listContains from '@/utils/custom_filters'


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

export default {
  name: 'ExpressionOutlier',
  components: {
    Chart
  },
  data() {
    return {
      myChart: null as any,
      chartData: null as any,
      chartOptions: null as any,
      table: [] as Array<ExpressionOutlier>,
    };
  },
  computed: {
    ...mapStores(useGlobalStore),
    filters() {
      return {
          global: { value: this.globalStoreStore.globalNav.gene, matchMode: FilterMatchMode.CONTAINS },
      }
    }
  },
  async mounted() {
    this.table = await $fetch("/api/assay/isoform/cutoff/99");

    this.chartData = this.setChartData();
    this.chartOptions = this.setChartOptions();
  },
  methods: {
    rowClick(event) {
      let data = event.data;
      let gene = data.associated_gene;
      let store = useGlobalStore();
      store.addGene(gene)
    },
    select(e) {
      console.log(e)
    },
    setChartData() {
      let samples = this.table.map((item: ExpressionOutlier) => {
          return item.Sample
        })
        .filter((value, index, array) => {
        return array.indexOf(value) === index; // does this existing in the filter array already?
      })

      let sampleDataSet = []

      // Parsing information per object
      let parsing = {
        xAxisKey: 'z_score',
        yAxisKey: 'test_statistic'
      }

      for (let sample of samples) {
        let dataObj : any = {}
        dataObj['label'] = sample;
        dataObj['parsing'] = parsing
        dataObj['data'] = this.table.filter((item: any) => {
          return item.Sample === sample;
        });
        sampleDataSet.push(dataObj);
      }

      console.table(sampleDataSet)

      return {
        datasets: sampleDataSet,
        parsing: {
          xAxisKey: 'z_score',
          yAxisKey: 'test_statistic'
        }
      };
    },
    setChartOptions() {
      const documentStyle = getComputedStyle(document.documentElement);
      const textColor = documentStyle.getPropertyValue('--p-text-color');

      return {
        AonClick: (event) => {
          let chart = this.myChart;

          const points = chart.getElementsAtEventForMode(
              event,
              'nearest', // Finds the closest element
              { intersect: true }, // Only consider elements directly under the cursor
              false
          );

          console.log(points)

          if (points.length) {
            const firstPoint = points[0];
            const datasetIndex = firstPoint.datasetIndex;
            const dataIndex = firstPoint.index;

            // Get the clicked point's data object
            const dataset = this.chartData.datasets[datasetIndex];
            const dataPoint = dataset.data[dataIndex];

            console.log('Clicked Point Data:', dataPoint);
            alert(`You clicked on: ${JSON.stringify(dataPoint)}`);
          }
        },
        plugins: {
          legend: {
            labels: {
              usePointStyle: true,
              color: textColor
            }
          }
        }
      };
    }
  }
};
</script>

<style scoped>
:deep(.centered>canvas) {
  margin-left: auto;
  margin-right: auto;
}


</style>