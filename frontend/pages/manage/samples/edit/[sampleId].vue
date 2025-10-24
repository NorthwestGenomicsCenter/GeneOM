<template>
  <div class="bordered">
    <div class="details-container">
      <section class="details">
        <FloatLabel variant="on" class="input">
          <InputText id="name" class="w-full" v-model="sample.name"/>
          <label for="name">Name</label>
        </FloatLabel>

        <FloatLabel variant="on" class="input">
          <InputText id="project-name" class="w-full" v-model="sample.externalProjectName"/>
          <label for="project-name">Project Name</label>
        </FloatLabel>

        <FloatLabel variant="on" class="input">
        <TextArea
            id="description"
            v-model="sample.description"
            rows="5" cols="60"
            class="w-full"/>
          <label for="project-name">Description</label>
        </FloatLabel>
      </section>
      <section class="details">
        <div>

        </div>
        <h3>Upload Data Files</h3>
        <FileUpload
            name="file"
            :url="`${baseUrl}/api/omic/upload`"
            :multiple="true"
            :maxFileSize="1000000000"
            @beforeUpload="beforeUpload"
            @upload="afterUpload"
            @error="uploadError" >
          <template #empty>
            <p>Drag and drop files to here to upload.</p>
          </template>
        </FileUpload>
        <br />
        <h3>Add File from Filesystem</h3>
        <Button size="small" label="Add File" outlined raised @click="addVisible = true" />
      </section>
    </div>

    <h4>Tags</h4>
    <div
        v-if="sample.tags != null && sample.tags.length > 0"
        v-for="[index, tag] of sample.tags.entries()"
        class="tag-input"
        :key="index">
      <FloatLabel variant="on" class="input">
        <Dropdown
            class="input-width"
            :options="['Tissue', 'Technology', 'Project', 'Something Else']"
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
    <SampleFilesTable :files="sample.omicResources" />
<!--    <template-->
<!--        class="file-group"-->
<!--        v-for="file of sample.omicResources"-->
<!--        :key="index">-->
<!--      <Resource-->
<!--          :editing="false"-->
<!--          :resource="file"-->
<!--          :visualizer-options="transformers" />-->
<!--    </template>-->
<!--    <Button label="Save Changes" @click="save"/>-->
  </div>

  <Dialog header="AddFile" modal v-model:visible="addVisible" :style="{ 'min-width': '65rem', 'max-width': '95rem' }">
    <VueFinder
        id="vf"
        :features="[]"
        :request="`${baseUrl}/api/vuefinder/search/`"
        @select="handleSelectEvent" />
    <Button label="Add Files" @click="addFiles" />
  </Dialog>

  <Toast />
</template>

<script setup lang="ts">
import Resource from "~/components/samples/Resource.vue";
import authClient from "~/plugins/auth.client";
import SampleFilesTable from "~/components/common/SampleFilesTable.vue";
import type {FileUploadBeforeUploadEvent, FileUploadErrorEvent} from "primevue";

// Setup the page
const router = useRoute();
const toast = useToast();

let baseUrl = ref(useRuntimeConfig().app.baseURL);
if (baseUrl.value === "/") {
  baseUrl.value = "";
}

const beforeUpload = async (e : FileUploadBeforeUploadEvent) => {
  e.formData.set("sampleId", sample.value.id);
}

const afterUpload = async () => {
  await refresh();
}

const uploadError = async (e : FileUploadErrorEvent) => {
  toast.add({
    severity: 'error',
    summary: "Upload Error",
    detail: e.xhr.response,
    life: 15000
  })
}

const selectedFiles = ref<FinderFileDTO[]>([]);
const handleSelectEvent = (selection : FinderFileDTO[]) => {
  selectedFiles.value = selection
}

const { data : sample, refresh } : { data: Ref<SampleDTO>, refresh: Function}
    = await useApi<SampleDTO>(`/sample/${router.params.sampleId}`, {
      method: "GET"
    })

// Get the list of available transformers for the data
const { data: transformers } : { data: Ref<string[]> } = await useApi("/omic/transformers")

// Add resources to the sample
const visible = ref<boolean>(false);
const addVisible = ref(false)
const addFiles = async () => {

  const body = [] as OmicResourceDTO[];
  for (const file of selectedFiles.value) {
    let omicResource = {
      omicResourceId: null,
      sampleId: sample.value.id,
      igvTrack: false,
      integrationSource: "file",
      path: file.path,
      visualizers: [],
      tissue: null,
      visualizationComponents: null
    }
    body.push(omicResource);
  }

  // const body : OmicResourceDTO = {
  //   omicResourceId: null,
  //   sampleId: sample.value.id,
  //   igvTrack: false,
  //   integrationSource: "file",
  //   path: selectedFiles.value[0].path,
  //   visualizers: []
  // }

  const response = await useClientApi<string>(`/omic/save`, {
    method: "POST",
    body: body
  })

  toast.add({severity: 'success', summary: response, life: 5000})
  await refresh();
}

// Tags
const deleteTag = async (tag) => {
  const res = await useApi<SampleDTO>(`/sample/tag/${tag.id}`, {
    method: "DELETE",
    body: sample.value
  })

  sample.value = res.data.value;
}

const addTag = () => {
  const newTag = {} as SampleTagDTO;
  sample.value.tags.push(newTag);
}

// Save Changes
const save = async () => {
  const res = await useApi(`/sample/save`, {
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
.details-container {
  display: flex;
  gap: 35px;
}

.details {
  width: 45%;
}

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