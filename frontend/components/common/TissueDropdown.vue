<template>
  <Select
    v-model="selected"
    :options="options"
    class="w-80"
    optionLabel="name"
    optionValue="url"
    @change="selectionChanged"
  >
    <template #footer>
      <div class="p-3">
        <Button label="Add New" fluid severity="secondary" text size="small" icon="pi pi-plus" @click="searchAdditional"/>
      </div>
    </template>
  </Select>

  <Dialog
    v-model:visible="dialogVisible"
    header="Select Tissue"
    class="max-w-6xl"
    modal >
    <TissueTable
      v-model:search-text="searchText"
      @updated="updated"
      show-select-column />
    <Button label="Add" @click="addTissue" />
  </Dialog>
</template>

<script setup lang="ts">
import { useTissueStore } from "~/stores/tissueStore";
import TissueTable from "~/components/management/TissueTable.vue";

const selected = defineModel();
const tissueStore = useTissueStore()
await tissueStore.fetchData()
const options = computed<TissueDTO[]>(() => tissueStore.data)

// Emit changes to parent component
const emit = defineEmits(['change'])
const selectionChanged = (event : SelectChangeEvent) => {
  emit('change', event.value)
}

// Add additional values to the dropdown box
const dialogVisible = ref<boolean>(false);
const searchText = ref<string>("");
const searchAdditional = (event) => {
  dialogVisible.value = true;
}

const addTissue = async () => {
  if (addFromTable.value != null) {
    await tissueStore.addTissue(addFromTable.value)
    selected.value = addFromTable.value
  }
}

const addFromTable = ref();
const updated = (update) => {
  addFromTable.value = update;
}
</script>

<style scoped>

</style>