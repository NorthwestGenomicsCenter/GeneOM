<template>
  <FileUpload
    mode="basic"
    auto
    url="/api/analysis/import/tabular-data"
    @upload="afterUpload" />
  <Dialog header="Select Columns" v-model:visible="selectColumnsDialogVisible" modal>
    <DataTable
      :value="data">
      <Column selectionMode="multiple" />
      <Column header="Header Name" field="headerName"/>
      <Column header="Column Type" field="columnType"/>
      <Column header="Confidence Score" field="score"/>
      <Column header="Example Values" field="sampleValues">
        <template #body="{data}">
          <template v-if="data != null">
            {{ data.sampleValues.join(', ') }}
          </template>
        </template>
      </Column>
    </DataTable>
    <div class="flex justify-end pt-4">
      <Button label="Import" />
    </div>

  </Dialog>
</template>

<script setup lang="ts">
const selectColumnsDialogVisible = ref<boolean>(false);

const emit = defineEmits(['uploaded']);

const data = ref();
const afterUpload = (event) => {
  selectColumnsDialogVisible.value = true;
  data.value = JSON.parse(event.xhr.response);
  emit('uploaded', data.value);
}
</script>

<style scoped>

</style>