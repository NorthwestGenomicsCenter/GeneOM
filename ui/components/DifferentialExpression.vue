<template>
  <Header title="Differential Expression" v-model="applyGlobals" />
  <div>
    <Chart
        type="scatter"
        :data="computeChartData"
        :options="chartOptions"
        :width="1000"
        :height="400"
        @select="select"
        class="chart centered"
        ref="differentialExpressionChart"
    />
  </div>
  <div>
    <DataTable
        :value="table"
        :paginator="true"
        :rows="10"
        :filters="filters"
        row-hover
        filterDisplay="menu"
        @row-dblclick="rowClick">
      <Column field="gene_name" header="gene" sortable/>
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

</template>

<script setup lang="ts">
import Chart from 'primevue/chart'
import Header from "~/components/common/Header.vue"
import GeneSelectPopover from "~/components/common/GeneSelectPopover.vue"

import {useGlobalStore} from '@/composables/useGlobal'
import {FilterMatchMode, FilterOperator} from "@primevue/core";

const op = ref();
const anchor = ref();
const genesList = ref<string[]>([]);

interface DifferentialExpression {
  transcript: string
  logFC: Number
  logCPM: Number
  F: Number
  PValue: Number
  FDR: Number
  gene_name: string
}

defineOptions({
  name: 'DifferentialExpression'
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
  chartRawData.value = await $fetch("/api/assay/differential_expression/chart/1");

  filterLimits.value = await $fetch("/api/assay/differentialPeak/filterLimits/1");

  // chartData.value = computeChartData();
  chartOptions.value = setChartOptions();
});


const store = useGlobalStore();

const filters = computed(() => {
  const geneFilterList = store.globalNav.genes
      .map(g => {
        return {
          value: g,
          matchMode: FilterMatchMode.EQUALS
        }
  })

  return {
    global: { value: store.globalNav.gene, matchMode: FilterMatchMode.CONTAINS },
    gene_name: { operator: FilterOperator.OR, constraints: applyGlobals.value ? geneFilterList : [] },
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

const computeChartData : any = computed(() => {
  if (chartRawData.value === undefined || chartRawData.value?.length == 0) return [];

  const selectGenes = store.globalNav.genes;
  // Parsing information per object
  let parsing = {
    xAxisKey: 'logFC',
    yAxisKey: 'logCPM',
  }

  const result : Record<string, DifferentialExpression[]> = {};
  for (const gene of selectGenes) {
    result[gene] = [];
  }

  result["Unlabeled"] = [];

  chartRawData.value.forEach(point => {
    if (selectGenes.some(el => point.geneNames.includes(el))) {
      for (let pointGene of point.geneNames) {
        if (selectGenes.includes(pointGene)) {
          result[pointGene].push(point);
          return
        }
      }
    }

    result["Unlabeled"].push(point)
  })

  const sampleDataSet = [];
  for (let [key, rows] of Object.entries(result)) {
    const position = {
      label: key,
      parsing: parsing,
      data: rows,
      backgroundColor: getGroupColor(key),
    }

    sampleDataSet.push(position)
  }

  return {
    datasets: sampleDataSet,
    parsing: parsing
  };
})

const chartOnClick = (event) => {
  let chart = differentialExpressionChart.value.chart;

  const points = chart.getElementsAtEventForMode(
      event,
      'nearest', // Finds the closest element
      {intersect: false}, // Only consider elements directly under the cursor
      false
  );

  if (points.length) {

    const firstPoint = points[0];
    const datasetIndex = firstPoint.datasetIndex;
    const dataIndex = firstPoint.index;

    // Get the clicked point's data object
    const dataset = computeChartData.value.datasets[datasetIndex];
    const dataPoint = dataset.data[dataIndex];

    // sort the gene name list and then push to the popup for selection
    let genes = dataPoint.geneNames.sort();

    genesList.value = []
    genesList.value.push(...genes);
    //store.addGene(dataPoint.geneNames[0])

    // Position the popover
    let pointerEvent = event.native;
    const meta = chart.getDatasetMeta(datasetIndex);
    const point = meta.data[dataIndex];
    const { x, y } = point.getProps(['x', 'y']); // Get pixel coordinates

    // Get the canvas's position on the page
    const canvasRect = chart.canvas.getBoundingClientRect();

    // Calculate absolute page coordinates
    const pageX = canvasRect.left + x + window.scrollX - 20;
    const pageY = canvasRect.top + y + window.scrollY;

    let el : HTMLElement = anchor.value;
    el.style.position = 'absolute';
    el.style.left = `${pageX}px`;
    el.style.top = `${pageY}px`;
    el.style.width = '0px';
    el.style.height = '0px';
    op.value.toggle(pointerEvent, el);

  }
}

function setChartOptions() {
  const documentStyle = getComputedStyle(document.documentElement);
  const textColor = documentStyle.getPropertyValue('--p-text-color');

  return {
    onClick: chartOnClick,
    maintainAspectRatio: false, // this is needed to keep the chart from shrinking to nothing
    animation: {
      duration: 0 // General animation setting
    },
    plugins: {
      tooltip: {
        callbacks: {
          label: function (context) {
            // Get the data point
            const dataPoint = context.raw;
            // Return the custom tooltip text
            return `Gene(s): ${dataPoint.geneNames}`;
          }
        },
        legend: {
          display: false,
          labels: {
            usePointStyle: true,
            color: textColor
          }
        }
      }
    }
  }
}
</script>

<style scoped>
:deep(.centered>canvas) {
  margin-left: auto;
  margin-right: auto;
}
</style>