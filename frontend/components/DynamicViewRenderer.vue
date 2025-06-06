<!-- DynamicViewRenderer.vue -->
<template>
  <component
      :is="ViewComponent"
      v-bind="componentProps"
      v-if="ViewComponent"
  />
  <div v-else>
    Unsupported view: {{ viewName }}
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { viewComponentMap } from '@/utils/viewComponentRegistry';

const props = defineProps({
  viewName: String,
  componentProps: Object,
});

const ViewComponent = computed(() => viewComponentMap[props.viewName]);
</script>
