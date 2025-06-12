<template>
  <SingleSampleViewer :sampleId="sampleId" />
</template>

<script setup lang="ts">
import SingleSampleViewer from '~/components/samples/SingleSampleViewer.vue'
const sampleId = useRoute().params.sampleId as string
</script>
