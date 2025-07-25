<template>
  <div class="w-full h-full overflow-auto p-4 bg-white rounded shadow">
    <template v-if="ViewComponent">
      <ClientOnly fallback-tag="h3" fallback="Loading...">
        <component :is="ViewComponent" v-bind="dynamicComponentProps" />
      </ClientOnly>
    </template>
    <template v-else>
      <div>Unsupported view: {{ viewName }}</div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { viewComponentMap } from '@/utils/viewComponentRegistry'

const props = defineProps({
  viewName: String,
  componentProps: Object,
})

const dynamicComponentProps = computed(() => props.componentProps)

const ViewComponent = computed(() =>
    viewComponentMap[props.viewName]
)
</script>
