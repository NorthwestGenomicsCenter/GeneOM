<template>
  <DataTable
    :value="table"
    class="w-7xl min-h-72"
  >
    <Column header="Chromosome" field="chromosome" />
    <Column header="Start" field="start" />
    <Column header="End" field="end" />
    <Column header="Notes" field="notes" />
    <Column header="Navigate">
      <template #body="{data}">
        <Button
          label="Go"
          severity="secondary"
          outlined
          @click="nav(data)"
        />
      </template>
    </Column>
  </DataTable>
</template>

<script setup lang="ts">
const global = useGlobalStore();
const {currentGenomicPosition: pos} = storeToRefs(global)

const props = defineProps<{
  table: any[]
}>();
const visible = defineModel<boolean>("visible");

const nav = (row) => {
  const position : GenomicPosition = {
    chr: row.chromosome,
    start: row.start,
    end: row.end
  }

  global.navigate(position)
  visible.value = false;
}

</script>

<style scoped>
.notes-text-area {
  width: 80ch;
  height: 20ch;
}

.text-right {
  margin-left: auto;
}
</style>