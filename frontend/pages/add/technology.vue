<template>
  <div>
    <h1>Add Technology</h1>
    <AddManagedTagForm endpoint="/api/technology" :existing-names="existingNames" />
  </div>
</template>

<script setup>
import AddManagedTagForm from '@/components/management/AddManagedTagForm.vue'
import { useFetch } from '#app'

const { data: existingNamesData } = await useFetch('/api/technology/names')
const existingNames = existingNamesData.value || []
</script>
