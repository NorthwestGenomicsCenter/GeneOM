<template>
  <SplitButton
      label="Add"
      rounded
      :model="addButtonOptions" />
</template>

<script setup lang="ts">
import {useSampleViewStore} from "~/composables/useSampleViewStore";
import {visualizers} from '~/components/vizualizer/registry'

const viewStore = useSampleViewStore();
const { addView } = viewStore;

const addButtonOptions = computed(() => {
  return visualizers
      .map(viz => ({
        label: viz.label,
        command: () => console.log(viz.component)
      }))
})
</script>

<style scoped>
</style>
