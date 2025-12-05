<template>
  <div v-if="analysis != null && layout != null" :key="route.fullPath">
    <Dialog v-model:visible="resourceTableVisible" modal header="Select Resource" :style="{ width: '90rem' }">
      <ViewerResourceTable :tableData="analysis.resources" />
    </Dialog>

    <Button label="test" @click="test" />
    {{testVal}}

    <TechnologyViewSelector @add-view="addView" />
    <Button
      label="Save Layout"
      severity="info"
      outlined
      @click="saveLayout"
    />

    <Button
        label="Save Position"
        severity="info"
        outlined
        @click="savePositionDialog = !savePositionDialog"
    />

    <Button
        :label="`Saved Positions (${analysis.savedPositions.length})`"
        severity="info"
        outlined
        @click="restoreSavePositionDialog = !restoreSavePositionDialog"
    />

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

  <Dialog
    header="Save Genomic Position"
    v-model:visible="savePositionDialog"
    modal >
    <CommonSaveGenomicPosition :analysisId="analysisId" v-model:visible="savePositionDialog" />
  </Dialog>

  <Dialog
      header="Restore Genomic Position"
      v-model:visible="restoreSavePositionDialog"
      modal >
    <CommonRestoreGenomicPosition v-model:visible="restoreSavePositionDialog" :table="analysis.savedPositions" />
  </Dialog>

</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import {computed, ref} from 'vue'
import TechnologyViewSelector from '~/components/common/TechnologyViewSelector.vue'
import GridLayout from '~/components/GridLayout.vue'
import {useSampleViewStore} from "~/composables/useSampleViewStore";
import {useViewResourcesStore} from "~/composables/useViewResourcesStore";
import DynamicViewRenderer from "~/components/DynamicViewRenderer.vue";

// The analysis that is being worked on
const route = useRoute()
const analysisId = computed(() => route.params.id as string);

const toast = useToast();

const viewStore = useSampleViewStore();
const {
  selectedViews,
  layout,
  components
} = storeToRefs(viewStore);

const {
  setAnalysis,
  addView,
  setCustomReference,
  setSelectedView,
  updateLayout,
  reset
} = viewStore;

const resourceStore = useViewResourcesStore();
const {
  setResourceList,
  updateResourceState,
  state,
  getSaveResources
} = resourceStore;

const test = () => testVal.value = getSaveResources();
const testVal = ref();

const loaded = ref<boolean>(false);
const { data: analysis, refresh: refreshAnalysis } : { data: Ref<AnalysisViewDTO>, refresh: Function }
    = await useApi(`/analysis/view/${analysisId.value}`);
setResourceList(analysis.value.resources)

onMounted(async() => {
  await refreshAnalysis()
  await setAnalysis(analysis.value);

  // Restore a previous snapshot of the application
  const snapshot = await useClientApi(`/snapshot/analysis-layout/${analysisId.value}`);
  if (snapshot != null && snapshot.layout != null) {
    setSelectedView(JSON.parse(snapshot.layout));
    if (snapshot.selectedResources != null) {
      console.table(snapshot.selectedResources)
      updateResourceState(snapshot.selectedResources)
    }
  }

  loaded.value = true;
})

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

const saveLayout = async () => {
  const selectedResources = getSaveResources();

  const saveLayoutDto : SaveAnalysisLayoutDTO = {
    layout: JSON.stringify(toRaw(selectedViews.value)),
    selectedResources: selectedResources
  }

    await useClientApi(`/snapshot/analysis-layout/${analysisId.value.toString()}`, {
      method: "POST",
      body: saveLayoutDto
    })
  try {

    toast.add({
      severity: "secondary",
      summary: "Layout Saved",
      life: 2000
    })

  } catch (error) {
    toast.add({
      severity: "error",
      summary: "Trouble Saving Layout",
      detail: `Unable to save layout:\n ${error}`,
      life: 8000
    })
  }
}

// Save the position that is currently navigated to in the application
const savePositionDialog = ref<boolean>(false);
const restoreSavePositionDialog = ref<boolean>(false);

onBeforeUnmount(() => {
  reset();
  loaded.value = false;
  analysis.value = null;
})

const resourceTableVisible = ref<boolean>(false);

</script>
