<template>
  <InputText v-model="queryString" placeholder="Query..." />
  <section class="bordered centered">
    <DataTable
        selection-mode="single"
        v-model:expandedRows="expandedRows"
        data-key="sampleId"
        :value="sampleSearch"
        :rows="20"
        :rowsPerPageOptions="[20,30,50]">
      <Column expander style="width: 5rem" />
      <Column header="Sample Id" field="sampleId" />
      <Column header="Sample Id" field="multiomicsSampleId" />
      <Column header="Sample Name" field="sampleName" />
      <Column header="Source" field="integrationSource" />
      <Column header="Description" field="sampleDescription" />
      <Column header="Project Name" field="projectName" />
      <Column header="External Link" field="sourceUrl">
        <template #body="{data}">
          <a :href="data.sourceUrl">QC Data</a>
        </template>
      </Column>
      <Column header="Create">
        <template #body="{data}">
          <SplitButton
              label="Add All Sample Files"
              size="small"
              severity="secondary"
              outlined
              raised
              :model="importButtonOption(data)"
              @click="addAllDataToSample(data)" />
<!--          <Button-->
<!--              size="small"-->
<!--              severity="secondary"-->
<!--              outlined-->
<!--              raised-->
<!--              label="Add All Sample Files"-->
<!--              @click="addAllDataToSample(data)" />-->
        </template>
      </Column>
      <template #expansion="slotProps">
        <DataTable
            :value="slotProps.data.sampleFiles"
            :rows="20"
            :rowsPerPageOptions="[20,30,50]">
<!--          <Column header="Sample" field="sampleId" />-->
          <Column header="Path" field="path" />
          <Column header="Add Resource">
            <template #body="{data}">
              <Button
                  label="Add"
                  outlined
                  raised
                  severity="info"
                  :disabled="slotProps.data.multiomicsSampleId == null"
                  @click="addResource(data, slotProps.data)"/>
            </template>
          </Column>
        </DataTable>
      </template>
    </DataTable>
    <Button label="Search External Samples" @click="querySamples" outlined />
<!--    {{ sampleSearch }}-->
  </section>

</template>

<script setup lang="ts">
const queryString = ref<string>("");

const toast = useToast();

const emit = defineEmits(["update"]);

const { data: response, refresh } = await useApi(`/search/integrations/${queryString.value}`);

const sampleSearch = ref<ExternalSampleDTO[]>([]);
const querySamples = async () => {
  try {
    sampleSearch.value = await useClientApi(`/search/integrations/sample/${queryString.value}`);
  } catch (err) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Unable to Search for Sample.\n' + err, life: 8000 })
  }

}

const importButtonOption = (data) => {
  return [
    {
      label: 'Only Create Sample',
      command: () => createSample(data)
    }
  ];
}

const addAllDataToSample = async (data) => {
  await createSample(data, true)
}

const createSample = async (data, addDataFiles: boolean = false) => {
  let filePaths = [];
  if (addDataFiles) {
    filePaths = data.sampleFiles.map(e => e.path);
  }

  const externalSampleDTO : CreateSampleFromExternalDTO = {
    name: data.sampleName,
    integrationSource: data.integrationSource,
    externalLimsId: data.sampleId,
    externalProjectName: data.projectName,
    description: data.description,
    filePaths: filePaths
  };

  try {
    const res = await useApi('/sample/create-external-sample', {
      method: "POST",
      body: externalSampleDTO
    })
    toast.add({
      severity: 'success',
      summary: 'Created',
      detail: `Created Sample: ${externalSampleDTO.externalLimsId}`,
      life: 8000
    })

    emit("update")
  } catch (err) {
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: `Unable to Save External Sample.\n + ${err}`,
      life: 8000
    })
  }
}

const testing = ref();
const addResource = async (data, sample : SampleDTO) => {

  const resource : AddOmicsResourceDTO = {
    igvTrack: false,
    path: data.path,
    sampleId: sample.multiomicsSampleId,
    visualizers: []
  };

  try {
    const res = await useClientApi('/omic/save', {
      method: "POST",
      body: [ resource ]
    });

    toast.add({ severity: 'success', summary: 'Success', detail: 'Added Resource to Sample', life: 8000 })
  } catch (e) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Unable to Search for Sample', life: 8000 })
  }

}

// Row Expansion
const expandedRows = ref({});


</script>

<style scoped>

</style>