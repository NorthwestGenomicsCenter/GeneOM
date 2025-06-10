<template>
  <FloatLabel variant="on" class="input">
    <InputText id="name" v-model="sample.name"/>
    <label for="name">Name</label>
  </FloatLabel>

  <FloatLabel variant="on" class="input">
    <InputText id="project-name" v-model="sample.external_project_name"/>
    <label for="project-name">Project Name</label>
  </FloatLabel>

  <FloatLabel variant="on" class="input">
    <TextArea
      id="description"
      v-model="sample.description"
      rows="5" cols="60" />
    <label for="project-name">Description</label>
  </FloatLabel>

  <h4>Tags</h4>
  <div
    v-for="[index, tag] of sample.tags.entries()"
    class="tag-input"
    :key="index">
    <FloatLabel variant="on" class="input">
      <Dropdown
          class="input-width"
        :options="['Tissue', 'Technology', 'Something Else']"
        :id="`label-${index}`"
        v-model="tag.label"/>
      <label :for="`label-${index}`">Label</label>
    </FloatLabel>

    <FloatLabel variant="on" class="input tag-input-field">
      <InputText :id="`value-${index}`" v-model="tag.value"/>
      <label :for="`value-${index}`">Value</label>
    </FloatLabel>
    <em
        class="pi pi-times-circle delete-button"
        outlined
        :disabled="tag.id == null"
        @click="deleteTag(tag)"/>
  </div>
  <a
    id="add-tag"
    @click="sample.tags.push({})">
    Add Tag
  </a>

  <h4>Files</h4>
  <div
      class="file-group"
      v-for="[index, file] of sample.omic_resources.entries()"
      :key="index">
    <FloatLabel variant="on" class="input">
      <InputText :id="`value-${index}`" v-model="file.path" style="width: 120ch;"/>
      <label :for="`label-${index}`">File Path</label>
    </FloatLabel>
    <FloatLabel variant="on" class="input">
      <MultiSelect
          v-model="file.visualizers"
          style="width: 120ch;"
          class="input-width"
          display="chip"
          :id="`label-${index}`"
          :options="['Differential Expression', 'Technology', 'Something Else']" />
      <label :for="`label-${index}`">Visualization Type</label>
    </FloatLabel>
    <div>
      <Checkbox :id="`igv-check-${index}`" binary v-model="file.igv_track"/>
      <label :for="`igv-check-${index}`">&nbsp;&nbsp;Igv</label>
    </div>
  </div>
  <a
      id="add-tag"
      @click="sample.tags.push({})">
    Add Tag
  </a>

  <Button label="Save Changes" @click="save"/>

  <Toast />

</template>

<script setup lang="ts">
interface Sample {
  id: string;
  name: string;
  external_lims_id: string;
  description?: any;
  external_project_name: string;
  omic_resources: Omicresource[];
  users: any[];
  tags: any[];
}

interface Omicresource {
  id: number;
  path: string;
  viz_transformer: string;
  visualizers: string[];
  igv_track: boolean | null;
}

const sample = defineModel<Sample>("sample", {required: true});
const visible = defineModel<boolean>("visible", {required: true});

const deleteTag = async (tag) => {
  const res = await useFetch(`/api/sample/tag/${tag.id}`, {
    method: "DELETE",
    body: sample.value
  })

  sample.value = res.data.value;
}

const toast = useToast()

const save = async () => {
  const res = await useFetch(`/api/sample/save`, {
    method: "POST",
    body: sample.value
  })

  if (res.error.value) {
    toast.add({
      severity: "error",
      summary: 'Error',
      detail: `Unable to Save Sample Information`,
      life: 16000
    })
  } else {
    toast.add({
      severity: "success",
      summary: 'Saved',
      detail: `Updated the Sample Information`,
      life: 6000
    })
  }

  visible.value = false;
}
</script>

<style scoped>
#add-tag {
  display: block;
  font-size: small;
  padding-bottom: 1.75em;
}

.delete-button {
  margin-left: 0.5em;
  margin-top: auto;
  margin-bottom: auto;
}

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

.tag-input {
  display: flex;
}

.tag-input-field {
  padding-left: 0.5em;
}
</style>