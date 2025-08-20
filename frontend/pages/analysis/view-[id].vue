<template>
  <div>
    <Dialog v-model:visible="resourceTableVisible" modal header="Select Resource" :style="{ width: '90rem' }">
      <ViewerResourceTable :tableData="analysis.resources" />
    </Dialog>

    <Button label="View" @click="resourceTableVisible = true" />
    <Button label="test" @click="addView({viewName: 'Igv'})" />
    <Button label="Cluster" @click="addView({viewName: 'ClusterGrammer'})" />
    <Button label="JuiceBox" @click="addView({viewName: 'JuiceBox'})" />

<!--    <VizualizerJuiceBox />-->

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

// The analysis that is being worked on
const route = useRoute()
const analysisId = computed(() => route.params.id);

const viewStore = useSampleViewStore();
const {
  sampleId,
  techViewOptions,
  selectedViews,
  layout,
  components,
  igvTracks
} = storeToRefs(viewStore);

const {
  addView,
  removeView,
  updateLayout,
  setSampleId,
  addAnalysisResource,
  setCustomReference
} = viewStore;

const { data: analysis, refresh } : { data: Ref<AnalysisViewDTO>, refresh: Function }
    = await useApi(`/analysis/view/${analysisId.value}`);

const message = ref<string>();
let eventSource = null;
onMounted(() => {
  let baseUrl = useRuntimeConfig().app.baseURL;
  if (baseUrl === "/") {
    baseUrl = "";
  }

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


const resourceTableVisible = ref<boolean>(false);


</script>
