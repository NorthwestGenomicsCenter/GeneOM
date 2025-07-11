<template>
  <div>
    <h1>Sample: {{ sampleId }}</h1>

    <TechnologyViewSelector
        :tech-view-options="techViewOptions"
        @add-view="addView"
    />

    <ClientOnly>
      <GridLayout
          :layout="layout"
          :components="components"
          :col-num="30"
          @update:layout="updateLayout"
      />
    </ClientOnly>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { ref, onMounted, computed } from 'vue'
import TechnologyViewSelector from '~/components/TechnologyViewSelector.vue'
import GridLayout from '~/components/GridLayout.vue'
import DynamicViewRenderer from '~/components/DynamicViewRenderer.vue'

const route = useRoute()
const sampleId = route.params.sampleId as string

const techViewOptions = ref<SampleViewDTO[]>([]) // populated from backend
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


function addView(view) {
  selectedViews.value.push({
    id: crypto.randomUUID(), // unique per view instance
    viewName: view.viewName,
    technology: view.technology,
    x: 0,
    y: 0,
    w: 10,
    h: 15,
  })
}

function removeView(viewId: string) {
  selectedViews.value = selectedViews.value.filter(v => v.id !== viewId)
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
