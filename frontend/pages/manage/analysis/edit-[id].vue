<template>
  <div class="cohort-editor bordered">
    <h1 class="page-title">Edit Analysis</h1>
    <form @submit.prevent="updateAnalysis">
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

    <h3 class="pt-5">Users:</h3>
    <div class="pt-3">
      <Tag
        v-for="user in analysis.users"
        key="id"
        severity="info"
        class="mr-2"
      >
        {{user.firstName}} {{user.lastName}}</Tag>
    </div>
    <div class="pt-3">
      <Button
          label="Modify Users"
          severity="secondary"
          outlined
          raised
          @click="addUsersDialogEnabled = true" />
    </div>
  </div>

  <Dialog
    header="Add Users to Analysis"
    v-model:visible="addUsersDialogEnabled"
    modal
  >
    <div class="flex justify-center p-6">
      <UsersSelectUsers
          v-model:selected-users="analysis.users" />
    </div>
    <div class="flex justify-end">
      <span>
        <Button label="Cancel" @click="addUsersDialogEnabled = false; refresh()"/>
      </span>
        <span>
        <Button label="Save" @click="addUsers" />
      </span>
    </div>
  </Dialog>

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
      <Column field="externalLimsId" header="External Sample ID" :sortable="true" />
      <Column field="description" header="Description" :sortable="true" />
      <Column field="externalProjectName" header="External Project" :sortable="true" />
      <Column field="files" header="Data Files" :sortable="true">
        <template #body="{data}">
          {{ data.omicResources?.length }}
        </template>
      </Column>
      <Column header="Delete" :sortable="true">
        <template #body="{data}">
          <em class="pi pi-times" @click="confirmRemoveSample($event, data)"/>
        </template>
      </Column>
      <Column
          header="Actions"
          :body="actionTemplate"
          style="width: 150px"
      />
    </DataTable>

    <Dialog v-model:visible="addSampleDialogVisible" header="Add Sample" modal style="max-width: 90vw;">
      <ClientOnly>
        <form @submit.prevent="addSample">
          <CommonSamplesTable
              :samples="allSamples"
              v-model:selection="selectedSamples">
            <template #left>
              <Column selectionMode="multiple" headerStyle="width: 3rem" />
            </template>
          </CommonSamplesTable>
<!--          <CommonSamplesTable @update="handleUpdate" />-->
          <div class="mt-4 flex justify-end gap-2">
            <Button label="Cancel" severity="secondary" @click="addSampleDialogVisible = false" />
            <Button label="Add" type="submit" />
          </div>
        </form>
      </ClientOnly>
    </Dialog>

    <Dialog v-model:visible="showAddCohortDialog" header="Add Cohort" modal style="max-width: 90vw;">
      <ClientOnly>
      <form @submit.prevent="addCohort">
        <CommonCohortTable @update="handleCohortUpdate" />
        <div class="mt-4 flex justify-end gap-2">
          <Button label="Cancel" severity="secondary" @click="showAddCohortDialog = false" />
          <Button label="Add" type="submit" />
        </div>
      </form>
      </ClientOnly>
    </Dialog>
  </div>

  <ConfirmPopup />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { useToast } from 'primevue/usetoast'

const route = useRoute()
const toast = useToast()

const analysisId = route.params.id as string

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

// Load the analysis information
const { data: analysis, refresh } = useApi<AnalysisEditDTO>(`/analysis/edit/${analysisId}`, {
  default: () => ({} as AnalysisEditDTO)
})

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

    toast.add({ severity: 'success', summary: 'Success', detail: `Reference Added: ${res.data}`, life: 3000 })
  } catch (err) {
    toast.add({ severity: 'error', summary: 'Something when wrong', detail: `${err}`, life: 13000 })
  }
}


// Load the samples
const loading = ref(false)

// Select the new samples that are part of this cohort
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

    addSampleDialogVisible.value = false
    await refresh()
  } catch (err) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to add sample', life: 3000 })
  }
}

import { useConfirm } from "primevue/useconfirm";
const confirm = useConfirm();

const confirmRemoveSample = (event, row : SampleDTO) => {
  confirm.require({
    target: event.currentTarget,
    message: 'Are you sure you want to remove this sample?',
    icon: 'pi pi-exclamation-triangle',
    rejectProps: {
      label: 'Cancel',
      severity: 'secondary',
      outlined: true
    },
    acceptProps: {
      label: 'Remove'
    },
    accept: () => {
      removeSample(row);
    }
  });
};

const removeSample = async (row : SampleDTO) => {
  console.table(row)
  try {
    await useClientApi(`/analysis/${analysisId}/${row.id}`, {
      method: 'DELETE',
    })
    toast.add({ severity: 'success', summary: 'Success', detail: 'Sample Removed', life: 3000 })

    await refresh()
  } catch (err) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to remove sample', life: 3000 })
  }
}


// Update the analysis, change the values of the data
const updateAnalysis = async () => {
  const a = analysis.value;

  const body : UpdateAnalysisDTO = {
    name: a.name,
    description: a.description,
    referenceGenome: a.referenceGenome
  };

  try {
    const res = await useApi(`/analysis/${analysisId}/update`, {
      method: 'POST',
      body: body,
    })

    if (res.error.value === null) {
      toast.add({ severity: 'success', summary: 'Success', detail: 'Updated the analysis', life: 3000 })
      await refresh()
    } else {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to update analysis', life: 3000 })
    }
  } catch (err) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to update analysis', life: 3000 })
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

  await useClientApi(`/analysis/add-cohort`,{
    method: 'POST',
    body: dto
  })
}

//-- Add Users ----------------------------------------------------------------
const addUsersDialogEnabled = ref<boolean>(false);
const addUsers = async () => {
  const body : AddAnalysisUsersDTO = {
    analysisId: analysisId,
    users: analysis.value.users
  };

  try {
    await useClientApi("/analysis/add/users", {
      method: 'POST',
      body: body
    })

    toast.add({severity: "info", summary: "Saved", life: 1000});
    await refresh();

  } catch (error) {
    toast.add({severity: "error", summary: "Unable to Add Users", life: 1000});
  }

  addUsersDialogEnabled.value = false;
}

</script>

<style scoped>
.cohort-editor {
  margin: 1rem;
  padding: 2rem;
}

.page-title {
  padding-bottom: 3em;
}
</style>
