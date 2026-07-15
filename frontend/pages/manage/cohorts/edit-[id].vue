<template>
  <div class="cohort-editor bordered">
    <h1>Edit Cohort</h1>
    <form @submit.prevent="updateCohort">
      <div class="flex flex-col gap-8 max-w-3xl pb-4">
        <FloatLabel>
          <InputText id="name" v-model="cohort.name" class="w-full" />
          <label for="name">Name</label>
        </FloatLabel>

        <FloatLabel>
          <Textarea
            id="description"
            v-model="cohort.description"
            :rows="6"
            class="w-full" />
          <label for="description">Description</label>
        </FloatLabel>

        <Button label="Update" type="submit" />
      </div>
    </form>

    <h3>Upload Data Files</h3>
    <FileUpload
        name="file"
        :url="`${baseUrl}/api/cohort/upload-resource/${cohortId}`"
        :multiple="true"
        :maxFileSize="1000000000"
        @beforeUpload="beforeUpload"
        @upload="afterUpload"
        @error="uploadError" >
      <template #empty>
        <p>Drag and drop files to here to upload.</p>
      </template>
    </FileUpload>

    Quick and Dirty Display of Resources:
    {{cohort.omicResources}}

  </div>

  <div class="bordered">
    <DataTable
        :value="samples"
        stripedRows
        rowHover
        paginator
        sort-field="externalLimsId"
        :sort-order="1"
        :rows="10"
        :loading="loading"
        dataKey="id"
    >
      <Column field="externalLimsId" header="External Sample ID" :sortable="true" />
      <Column field="description" header="Description" :sortable="true" />
      <Column field="externalProjectName" header="External Project" :sortable="true" />
      <Column field="files" header="Data Files" :sortable="true" >
        <template #body="{data}">
          {{ data.omicResources?.length }}
        </template>
      </Column>
      <Column
          header="Actions"
          :body="actionTemplate"
          style="width: 150px"
      />
      <template #header>
        <div class="flex justify-end">
          <Button
            label="Add Sample"
            icon="pi pi-plus"
            @click="showAddSampleDialog" />
        </div>
      </template>
    </DataTable>


    <Dialog
        v-model:visible="addSampleDialogVisible"
        header="Add Sample"
        style="width: 100em;"
        modal>
      <form @submit.prevent="addSample">
        <CommonSamplesTable
            :samples="allSamples"
            v-model:selection="selectedSamples">
          <template #left>
            <Column selectionMode="multiple" headerStyle="width: 3rem" />
          </template>
        </CommonSamplesTable>
        <div class="mt-4 flex justify-end gap-2">
          <Button label="Cancel" severity="secondary" @click="addSampleDialogVisible = false" />
          <Button label="Add" type="submit" />
        </div>
      </form>
    </Dialog>
  </div>

</template>

<script setup lang="ts">
import {ref} from 'vue'
import {useRoute} from 'vue-router'
import {useToast} from 'primevue/usetoast'
import type {FileUploadBeforeUploadEvent, FileUploadErrorEvent} from "primevue";

const route = useRoute()
const toast = useToast()

let baseUrl = ref(useRuntimeConfig().app.baseURL);
if (baseUrl.value === "/") {
  baseUrl.value = "";
}

const cohortId = route.params.id as string

// -- Add Sample Dialog -------------------------------------------------------
const addSampleDialogVisible = ref(false)
const showAddSampleDialog = async () => {
  try {
    allSamples.value = await useClientApi<SampleDTO[]>("/sample/all");
    addSampleDialogVisible.value = true;
  } catch (error) {
    toast.add({severity: "error", summary: "Unable to Retrieve Samples List", detail: `${error}`, life: 8000})
  }
}
const allSamples = ref<SampleDTO[]>([]);
const selectedSamples = ref<SampleDTO[]>([]);

// Load the cohort information
const { data: cohort, refresh } = await useApi<CohortDTO>(`/cohort/${cohortId}`)

const beforeUpload = async (e : FileUploadBeforeUploadEvent) => {
  e.formData.set("sampleId", cohort.value.id);
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

// Load the samples
const loading = ref(false)
const samples = ref([])
const loadSamples = async () => {
  loading.value = true
  try {
    const res = await useApi(`/cohort/${cohortId}/samples`)
    samples.value = res.data.value
  } catch (err) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to load samples', life: 3000 })
  } finally {
    loading.value = false
  }
}
await loadSamples();

// Select the new samples that are part of this cohort
const addSample = async () => {
  try {
    const requestBody : AddCohortSampleDTO = {
      cohortId: cohortId,
      samples: selectedSamples.value,
    };

    await useApi(`/cohort/add/samples`, {
      method: 'POST',
      body: requestBody,
    })
    toast.add({ severity: 'success', summary: 'Success', detail: 'Sample added', life: 3000 })
    addSampleDialogVisible.value = false
    await loadSamples()
  } catch (err) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to add sample', life: 3000 })
  }
}

// Update the cohort, change the values of the data
const updateCohort = async () => {
  try {
    const res = await useApi(`/cohort/${cohortId}/update`, {
      method: 'POST',
      body: cohort.value,
    })

    if (res.error.value == null) {
      toast.add({ severity: 'success', summary: 'Success', detail: 'Updated the cohort data', life: 3000 })
      await refresh()
    } else {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to update cohort', life: 3000 })
    }
  } catch (err) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to update cohort', life: 3000 })
  }
}

const deleteSample = async (id: string) => {
  try {
    await useApi(`/cohort/${cohortId}/samples/${id}`, { method: 'DELETE' })
    toast.add({ severity: 'success', summary: 'Deleted', detail: 'Sample removed', life: 3000 })
    await loadSamples()
  } catch (err) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to delete sample', life: 3000 })
  }
}

const actionTemplate = (row: any) => {
  return h('Button', {
    icon: 'pi pi-trash',
    severity: 'danger',
    rounded: true,
    onClick: () => deleteSample(row.id),
  })
}
</script>

<style scoped>
h1 {
  color: #6e6e6e;
  font-size: 1.75rem;
  font-weight: bolder;
  font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
  margin-bottom: 1rem;
  text-align: center;
  width: 100%;
}

.cohort-editor {
  padding: 2rem;
}
</style>
