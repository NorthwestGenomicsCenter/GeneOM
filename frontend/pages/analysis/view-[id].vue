<template>
  <div v-if="analysis != null && layout != null">
    <Dialog v-model:visible="resourceTableVisible" modal header="Select Resource" :style="{ width: '90rem' }">
      <ViewerResourceTable :tableData="analysis.resources" />
    </Dialog>
    <SplitButton
      label="Add"
      :model="addButtonOptions"
      @click="resourceTableVisible = true" />

<!--    <TechnologyViewSelector-->
<!--        :tech-view-options="techViewOptions"-->
<!--        @add-view="addView"-->
<!--    />-->

    <ClientOnly>
      <GridLayout
          :layout="layout"
          :components="components"
          :col-num="30"
          :selected-views="selectedViews"
          @update:layout="updateLayout"
      />
    </ClientOnly>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import TechnologyViewSelector from '~/components/TechnologyViewSelector.vue'
import GridLayout from '~/components/GridLayout.vue'
import {useSampleViewStore} from "~/composables/useSampleViewStore";
import browser from "@interactjs/utils/browser";
import {useViewResourcesStore} from "~/composables/useViewResourcesStore";
import DifferentialExpression from "~/components/DifferentialExpression.vue";

// The analysis that is being worked on
const route = useRoute()
const analysisId = computed(() => route.params.id);

const viewStore = await useSampleViewStore();
const {
  analysis,
  selectedViews,
  layout,
  components
} = storeToRefs(viewStore);

const {
  addView,
  refreshAnalysis,
  setCustomReference,
  updateLayout
} = viewStore;

const addButtonOptions = [
  {
    label: 'Igv',
    command: () => addView({viewName: 'Igv'})
  },
  {
    label: 'Cluster',
    command: () => addView({viewName: 'ClusterGrammer'})
  },
  {
    label: 'JuiceBox',
    command: () => addView({viewName: 'JuiceBox'})
  },
  {
    label: 'Differential Peak',
    command: () => addView({viewName: 'differential-peak'})
  },
  {
    label: 'Differential Expression',
    command: () => addView({viewName: 'differential-expression'})
  },
  {
    label: 'Differential Expression Table',
    command: () => addView({viewName: 'DifferentialExpressionTable'})
  },
  {
    label: 'Tandem Repeats',
    command: () => addView({viewName: 'TandemRepeatsTransformer'})
  },
  {
    label: 'Tandem Graph',
    command: () => addView({viewName: 'TandemRepeatsGraph'})
  },
]

const resourceStore = useViewResourcesStore();
const { getResourceState, updateResourceState } = resourceStore;
resourceStore.$subscribe(async () => {
  const body = {
    updateTime: new Date(),
    message: JSON.stringify(getResourceState())
  }

  await useClientApi(`/analysis/update/resources/${analysisId.value.toString()}`, {
    method: "POST",
    body: body
  })
})

const message = ref<string>();
let eventSource = null;
onMounted(async () => {
  let baseUrl = useRuntimeConfig().app.baseURL;
  if (baseUrl === "/") {
    baseUrl = "";
  }

  await refreshAnalysis();
  updateResourceState(analysis.value.selectedResources);


  eventSource = new EventSource(`${baseUrl}/api/analysis/flux/${analysisId.value}`);
  eventSource.onmessage = (event) => {
    message.value = event.data;
    if (!message.value.includes("heartbeat")) {
      viewStore.$patch(JSON.parse(event.data));
    }
  };
  eventSource.onerror = (error) => {
    console.error('SSE Error:', error);
    eventSource.close();
  };

  const igvView : SampleViewDTO = {
    technology: "Igv",
    viewName: "Igv",
    omicResourceId: "IGV",
    fileName: "IGV"
  }

  if (analysis.value.customReference !== null) {
    setCustomReference(analysis.value.customReference);
  }

  const savedView = analysis.value.layout;
  if (savedView == null) {
    addView(igvView);
  } else {
    // Restore the saved view
    viewStore.$patch(JSON.parse(savedView));
  }

  watch(
      viewStore.$state,
      async (state) => {
        try {
          const body = {
            updateTime: new Date(),
            message: JSON.stringify(state)
          }

          await useClientApi(`/analysis/update/layout/${analysisId.value.toString()}`, {
            method: "POST",
            body: body
          })
        } catch (error) {
          console.log(error)
        }
      },
      { deep: true }
  )
})

onUnmounted(() => {
  if (eventSource) {
    eventSource.close()
  }
})


const resourceTableVisible = ref<boolean>(false);



</script>
