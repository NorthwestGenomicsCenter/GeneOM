<template>
  <div>
    <h1>Sample: {{ sampleId }}</h1>
    {{ selectedViews }}
    <TechnologyViewSelector
        :tech-view-options="techViewOptions"
        @add-view="addView"
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
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import TechnologyViewSelector from '~/components/common/TechnologyViewSelector.vue'
import GridLayout from '~/components/grid-layout/GridLayout.vue'
import {useSampleViewStore} from "~/composables/useSampleViewStore";

const route = useRoute()
const viewStore = useSampleViewStore();
const {
  sampleId,
  techViewOptions,
  selectedViews,
  layout,
  components,
} = storeToRefs(viewStore);

const { addView, removeView, updateLayout, setSampleId} = viewStore;
const sampleIdQuery = computed(() => route.query.samples);
setSampleId(sampleIdQuery.value);

</script>
