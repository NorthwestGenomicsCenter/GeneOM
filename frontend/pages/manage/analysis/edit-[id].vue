<template>
  <h1>Edit Analysis</h1>
  <div class="cohort-editor bordered">
    <form @submit.prevent="updateCohort">
      <div class="flex flex-col gap-8 max-w-3xl pb-4">
        <FloatLabel>
          <InputText id="name" v-model="analysis.name" class="w-full" />
          <label for="name">Name</label>
        </FloatLabel>

        <FloatLabel>
          <Textarea
            id="description"
            v-model="analysis.description"
            :rows="6"
            class="w-full" />
          <label for="description">Description</label>
        </FloatLabel>

        <FloatLabel>
          <InputText id="reference-genome" v-model="analysis.referenceGenome" class="w-full" />
          <label for="reference-genome">Reference Genome</label>
        </FloatLabel>

        <FloatLabel>
          <Select
            id="custom-reference-genome"
            v-model="analysis.referenceGenome"
            :options="customReferenceGenomes"
            class="w-full" />
          <label for="custom-reference-genome">Custom Reference Genome</label>
        </FloatLabel>

        <p>
          We want to be able to add custom genomes per Chee
        </p>
        Last Updated: {{ analysis.lastModified }}
        <Button label="Update" type="submit" />
      </div>
    </form>
    <Button label="Create Custom Reference" @click="createCustomReference" />

    quick and dirty cohorts:
    {{ analysis.cohorts }}
  </div>

  <div class="bordered">
    <DataTable
        :value="analysis.samples"
        stripedRows
        rowHover
        paginator
        sort-field="externalLimsId"
        :sort-order="1"
        :rows="10"
        :loading="loading"
        dataKey="id"
    >
      <template #header>
        <div class="flex justify-end">
          <Button
            severity="secondary"
            outlined
            raised
            label="Add Sample"
            icon="pi pi-plus"
            @click="showAddSampleDialog = true" />
          <Button
              severity="secondary"
              outlined
              raised
              label="Add Cohort"
              icon="pi pi-plus"
              @click="addCohort" />
        </div>
      </template>
      <Column field="externalLimsId" header="External Sample ID" sortable/>
      <Column field="description" header="Description" sortable/>
      <Column field="externalProjectName" header="External Project" sortable/>
      <Column field="files" header="Data Files" sortable>
        <template #body="{data}">
          {{ data.omicResources?.length }}
        </template>
      </Column>
      <Column
          header="Actions"
          :body="actionTemplate"
          style="width: 150px"
      />
    </DataTable>


    <Dialog v-model:visible="showAddSampleDialog" header="Add Sample" modal>
      <form @submit.prevent="addSample">
        <CommonSamplesTable @update="handleUpdate" />
        <div class="mt-4 flex justify-end gap-2">
          <Button label="Cancel" severity="secondary" @click="showAddSampleDialog = false" />
          <Button label="Add" type="submit" />
        </div>
      </form>
    </Dialog>

    <Dialog v-model:visible="showAddCohortDialog" header="Add Cohort" modal>
      <form @submit.prevent="addCohort">
        <CommonCohortTable @update="handleCohortUpdate" />
        <div class="mt-4 flex justify-end gap-2">
          <Button label="Cancel" severity="secondary" @click="showAddCohortDialog = false" />
          <Button label="Add" type="submit" />
        </div>
      </form>
    </Dialog>
  </div>

  <Toast />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { useToast } from 'primevue/usetoast'

const route = useRoute()
const toast = useToast()

const analysisId = route.params.id as string
const showAddSampleDialog = ref(false)

// Load the analysis information
const { data: analysis, refresh } = await useApi<AnalysisEditDTO>(`/analysis/edit/${analysisId}`)

// Custom Reference
const customReferenceGenomes = ref([
    "Chee Custom X"
])

const createCustomReference = async () => {
  const body : CreateCustomReferenceGenomeDTO = {
    shortName: "CheeX",
    longName: "Chee Custom X Chromosome",
    filePath: "/net/nwgc/vol1/nobackup/cheehong/multiomeBrowser/iPSC/genome/chrX.mutant.fa",
    indexPath: "/net/nwgc/vol1/nobackup/cheehong/multiomeBrowser/iPSC/genome/chrX.mutant.fa"
  }
  try {
    const res = await useClientApi(`/analysis/create/customReferenceGenome/${analysisId}`, {
      method: "POST",
      body: body
    })

    await toast.add({ severity: 'success', summary: 'Success', detail: `Reference Added: ${res.data}`, life: 3000 })
  } catch (err) {
    await toast.add({ severity: 'error', summary: 'Something when wrong', detail: `${err}`, life: 13000 })
  }
}


// Load the samples
const loading = ref(false)

// Select the new samples that are part of this cohort
const selectedSamples = ref<SampleDTO[]>([]);
const handleUpdate = (samples: SampleDTO[]) => {
  selectedSamples.value = samples
}
const addSample = async () => {
  try {
    const requestBody : AddAnalysisSampleDTO = {
      analysisId: analysisId,
      samples: selectedSamples.value,
    };

    await useClientApi(`/analysis/add/samples`, {
      method: 'POST',
      body: requestBody,
    })
    toast.add({ severity: 'success', summary: 'Success', detail: 'Sample added', life: 3000 })

    showAddSampleDialog.value = false
    await refresh()
  } catch (err) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to add sample', life: 3000 })
  }
}

// Update the cohort, change the values of the data
const updateCohort = async () => {
  try {
    const res = await useApi(`/cohort/${analysisId}/update`, {
      method: 'POST',
      body: analysis.value,
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
    await useApi(`/cohort/${analysisId}/samples/${id}`, { method: 'DELETE' })
    toast.add({ severity: 'success', summary: 'Deleted', detail: 'Sample removed', life: 3000 })
    await refresh()
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

// Add cohort information to the page
const showAddCohortDialog = ref<boolean>(false);
const selectedCohorts = ref([]);
const handleCohortUpdate = (cohorts : []) => {
  selectedCohorts.value = cohorts;
}

const addCohort = async () => {
  showAddCohortDialog.value = true;
  let cohortId = selectedCohorts.value[0].id;

  let dto = {
    analysisId: analysisId,
    cohortId: cohortId
  }

  const res = await useClientApi(`/analysis/add-cohort`,{
    method: 'POST',
    body: dto
  })
}

</script>

<style scoped>
.cohort-editor {
  padding: 2rem;
}
.controls {
  margin-bottom: 1rem;
}
</style>
