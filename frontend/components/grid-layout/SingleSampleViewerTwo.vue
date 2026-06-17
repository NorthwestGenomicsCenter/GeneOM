<template>
  <div>
    <h1>Sample: {{ sampleId }}</h1>

    <TechnologyViewSelector
        :tech-view-options="techViewOptions"
        @add-view="addView"
    />

    <ClientOnly>
      <CustomGridLayout
          :layout="layout"
          :components="components"
          :columns="30"
          :row-height="30"
          @update:layout="updateLayout"
          @add:view="addView"
      />
    </ClientOnly>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { ref, onMounted, computed } from 'vue'
import TechnologyViewSelector from '~/components/common/TechnologyViewSelector.vue'
import CustomGridLayout from '~/components/grid-layout/CustomGridLayout.vue'
import DynamicViewRenderer from '~/components/DynamicViewRenderer.vue'

const route = useRoute()
const sampleId = route.params.sampleId as string

const techViewOptions = ref([]) // populated from backend
const selectedViews = ref([])   // array of { id, viewName, technology, x, y, w, h }

onMounted(async () => {
  techViewOptions.value = await $fetch(`/api/sample/${sampleId}/views`)
})

// Compute layout from selectedViews
const layout = computed(() =>
    selectedViews.value.map(view => ({
      i: view.id,
      x: view.x,
      y: view.y,
      w: view.w,
      h: view.h,
    }))
)

const components = computed(() =>
    selectedViews.value.map(view => ({
      id: view.id,
      component: DynamicViewRenderer,
      props: {
        viewName: view.viewName,
        sampleId: sampleId,
        technology: view.technology,
      }
    }))
)

function addView(viewOrPosition) {
  if ('viewName' in viewOrPosition) {
    // called from TechnologyViewSelector
    selectedViews.value.push({
      id: crypto.randomUUID(),
      viewName: viewOrPosition.viewName,
      technology: viewOrPosition.technology,
      x: 0,
      y: 0,
      w: 10,
      h: 15,
    })
  } else {
    // called from click inside CustomGridLayout
    selectedViews.value.push({
      id: crypto.randomUUID(),
      viewName: 'default',
      technology: 'unspecified',
      x: viewOrPosition.x,
      y: viewOrPosition.y,
      w: viewOrPosition.w,
      h: viewOrPosition.h,
    })
  }
}

function updateLayout(newLayout) {
  newLayout.forEach(updated => {
    const match = selectedViews.value.find(v => v.id === updated.i)
    if (match) {
      match.x = updated.x
      match.y = updated.y
      match.w = updated.w
      match.h = updated.h
    }
  })
}
</script>
