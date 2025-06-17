<template>
  <div class="file-group">
    <FloatLabel variant="on" class="input">
      <InputText
        id="path"
        v-model="resource.path"
        :disabled="!editing"
        style="width: 120ch;"/>
      <label for="path">File Path</label>
    </FloatLabel>
    <FloatLabel variant="on" class="input">
      <MultiSelect
          v-model="resource.visualizers"
          style="width: 120ch;"
          class="input-width"
          display="chip"
          id="visualizers"
          :options="visualizerOptions" />
      <label for="visualizers">Visualization Type</label>
    </FloatLabel>
    <div>
      <Checkbox id="igv-check" binary v-model="resource.igvTrack"/>
      <label for="igv-check`">&nbsp;&nbsp;Igv</label>
    </div>
    <div>
      <em class="pi pi-times-circle" @click="removeFile"/>
      <label for="igv-check`">&nbsp;&nbsp;Remove</label>
      {{ resource }}
    </div>
  </div>
  <Toast />
</template>

<script setup lang="ts">

const toast = useToast();

interface Props {
  visualizerOptions: string[];
}
defineProps<Props>()

// Model values
const resource = defineModel<OmicResourceDTO>("resource", {required: true});
const editing = defineModel<boolean>("editing", {default: false});

// Remove resource from a sample
const removeFile = async () => {
  const res = await useFetch(`/api/omic/remove`, {
    method: "DELETE",
    body: resource
  })

  toast.add({severity: 'success', summary: res.data.value})
}
</script>

<style scoped>
.file-group {
  border: 1px solid #c3c3c3;
  border-radius: 8px;
  margin-bottom: 1em;
  padding: 1em;
}

.file-group:nth-of-type(2n+1) {
  background-color: #faf8ff;
}

.input-width {
  width: 12rem;
}

.input {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
}
</style>