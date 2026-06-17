<template>
  <div>
    <div class="de-container">
      <Header
          title="Differential Expression"
          v-model="applyGlobals"
          @settings="selectDialogVisible = true"
          @close="removeView(componentKey)"
      >
      </Header>

      <div class="chart-wrapper">
        <div class="popover-anchor" style="position: absolute" ref="anchor"></div>
        <Chart
            type="scatter"
            :data="computeChartData"
            :options="chartOptions"
            @select="select"
            class="chart centered"
            ref="differentialExpressionChart"
        />
      </div>

      <Dialog header="Select Samples" v-model:visible="selectDialogVisible" modal>
        <ViewerResourceTable
            :componentKey="componentKey"
            :visualizers="['Differential Expression']"
            v-model:visible="selectDialogVisible"
        />
      </Dialog>

      <GeneSelectPopover :genes="genesList" ref="op"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import Chart from 'primevue/chart'
import Header from "~/components/common/Header.vue"
import GeneSelectPopover from "~/components/common/GeneSelectPopover.vue"

import {useGlobalStore} from '~/composables/useGlobal'
import {FilterMatchMode, FilterOperator} from "@primevue/core";
import {useSampleViewStore} from "~/composables/useSampleViewStore";


const toast = useToast();
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
  geneName: string
}

defineOptions({
  name: 'DifferentialExpression'
})

const selectDialogVisible = ref<boolean>(false);
const props = defineProps<{
  componentKey: any
}>();

const viewStore = useSampleViewStore();
const {layout, customReference, analysis} = storeToRefs(viewStore);
const {removeView} = viewStore;

const resourcesStore = useViewResourcesStore();
const {selectedResources} = resourcesStore;
const resources = selectedResources(props.componentKey);

const chartRawData = ref<any>();
const updateResourceData = async () => {
  chartRawData.value = [];
  if (resources.value.length == 0) return

  try {
    chartRawData.value = await useClientApi(`/omic/visualize/transform/DiffExpressionVisualizer/${resources.value[0].resourceId}`);
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

const applyGlobals = ref<boolean>(false);
const chartOptions = ref<any>();
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
    global: {value: store.globalNav.gene, matchMode: FilterMatchMode.CONTAINS},
    geneName: {operator: FilterOperator.OR, constraints: applyGlobals.value ? geneFilterList : []},
  }
})

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

const select = async (event) => {
  console.table(event)
}

const computeChartData: any = computed(() => {
  if (chartRawData.value === undefined || chartRawData.value?.length == 0) return [];

  const selectGenes = store.globalNav.genes;
  // Parsing information per object
  let parsing = {
    xAxisKey: 'logFC',
    yAxisKey: 'logCPM',
  }

  const result: Record<string, DifferentialExpression[]> = {};
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

    // Position the popover
    let pointerEvent = event.native;

    const pageX = event.x - 18;
    const pageY = event.y + 2;

    let el: HTMLElement = anchor.value;
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
  position: relative;
}

.table-wrapper {
  flex: 1 1 40%;
  min-height: 200px;
  overflow: auto;
}

</style>