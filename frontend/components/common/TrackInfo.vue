<template>
  <p>This Displays</p>
  <p>Info about the track</p>
  <p>
    <Button
      outlined
      severity="info"
      label="Edit"
      @click="edit"/>
  </p>
</template>

<script setup lang="ts">
const emit = defineEmits<{
  edit: [id: AnalysisResource]
}>()

const props = defineProps<{
  resource: AnalysisResource
}>()

const edit = () => {
  emit("edit", props.resource)
}
</script>

<style scoped>

</style>