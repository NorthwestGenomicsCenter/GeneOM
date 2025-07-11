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
          <Button size="small" label="Create Sample" @click="createSample(data)" />
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
const queryString = ref<string>("1674062");
const response = ref<any>([]);

const query = async () => {
  const res = await useApi(`/search/integrations/${queryString.value}`);
  response.value = res.data.value;
}

const sampleSearch = ref<ExternalSampleDTO[]>([]);
const querySamples = async () => {
  const res = await useApi(`/search/integrations/sample/${queryString.value}`);
  sampleSearch.value = res.data.value;
}

const createSample = async (data) => {
  const dto : SampleDTO = {
    id: null,
    name: data.sampleName,
    integrationSource: data.integrationSource,
    externalLimsId: data.sampleId,
    externalProjectName: data.projectName,
    description: data.description,
    omicResources: null,
    tags: null,
    users: null
  }

  const res = await useApi('/sample/save', {
    method: "POST",
    body: dto
  })
}

const testing = ref();
const addResource = async (data, sample : SampleDTO) => {
  const res = await useApi('/omic/save', {
    method: "POST",
    body: {
      "sampleId": sample.multiomicsSampleId,
      "path": data.path,
      "type": "text_File"
    }
  });

  testing.value = res;
}

// Row Expansion
const expandedRows = ref({});


</script>

<style scoped>

</style>