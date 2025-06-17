<template>
  <FloatLabel variant="on" class="input">
    <InputText id="name" v-model="sample.name"/>
    <label for="name">Name</label>
  </FloatLabel>

  <FloatLabel variant="on" class="input">
    <InputText id="project-name" v-model="sample.externalProjectName"/>
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
    @click="addTag">
    Add Tag
  </a>

  <h4>Files</h4>
  <Button size="small" label="Add File" outlined raised @click="addVisible = true" />
  <template
      class="file-group"
      v-for="file of sample.omicResources"
      :key="index">
    <Resource
      :editing="false"
      :resource="file"
      :visualizer-options="['Differential Expression', 'Technology', 'Something Else']" />
  </template>
  <Button label="Save Changes" @click="save"/>

  <Dialog header="AddFile" modal v-model:visible="addVisible">
    <Resource :resource="{} as OmicResourceDTO" :visualizer-options="['Differential Expression', 'Technology', 'Something Else']" />
  </Dialog>

  <Toast />
</template>

<script setup lang="ts">
import Resource from "~/components/samples/Resource.vue";

const addVisible = ref(true)

const sample = defineModel<SampleDTO>("sample", {required: true});
const visible = defineModel<boolean>("visible", {required: true});

const deleteTag = async (tag) => {
  const res = await useFetch(`/api/sample/tag/${tag.id}`, {
    method: "DELETE",
    body: sample.value
  })

  sample.value = res.data.value;
}

const addTag = () => {
  const newTag = {} as SampleTagDTO;
  sample.value.tags.push(newTag);
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