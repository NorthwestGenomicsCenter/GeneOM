<template>
  <DataTable
    :value="cohorts"
    :selectAll="false"
    v-model:selection="selected"
    @rowSelect="rowSelect"
    @rowUnselect="rowSelect"
    >

    <Column selectionMode="multiple" headerStyle="width: 3rem"></Column>
    <Column header="Name" field="name" />
    <Column header="Description" field="description" />
  </DataTable>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {FilterMatchMode} from "@primevue/core";
import SampleChip from '~/components/samples/SampleChip.vue';

definePageMeta({
  layout: 'default'
})

const selected = ref<any>([]);

const { data: cohorts, refresh } = await useApi("/cohort/all");

const emit = defineEmits<{
  update: [selected: any]
}>()

const rowSelect = () => {
  emit("update", selected.value );
}

</script>

<style scoped>

</style>