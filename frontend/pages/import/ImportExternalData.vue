<template>
  <InputText v-model="queryString" placeholder="Query..." />
<!--  <section class="bordered centered">-->
<!--    <Button label="Query" @click="query" />-->
<!--    <DataTable-->
<!--      :value="response"-->
<!--      :rows="20"-->
<!--      :rowsPerPageOptions="[20,30,50]">-->
<!--      <Column header="Sample" field="sample_id" />-->
<!--      <Column header="Path" field="path" />-->
<!--      <Column header="Add Resource">-->
<!--        <template #body="{data}">-->
<!--          <Button-->
<!--            label="Add"-->
<!--            outlined-->
<!--            raised-->
<!--            severity="info"-->
<!--            @click="addResource(data)"/>-->
<!--        </template>-->
<!--      </Column>-->
<!--    </DataTable>-->
<!--  </section>-->

  <section class="bordered centered">
    <DataTable
        selection-mode="single"
        v-model:expandedRows="expandedRows"
        data-key="sample_id"
        :value="sampleSearch"
        :rows="20"
        :rowsPerPageOptions="[20,30,50]">
      <Column expander style="width: 5rem" />
      <Column header="Sample Id" field="sample_id" />
      <Column header="Sample Id" field="multiomics_sample_id" />
      <Column header="Sample Name" field="sample_name" />
      <Column header="Source" field="integration_source" />
      <Column header="Description" field="sample_description" />
      <Column header="Project Name" field="project_name" />
      <Column header="Create">
        <template #body="{data}">
          <Button size="small" label="Create Sample" @click="createSample(data)" />
        </template>
      </Column>
      <template #expansion="slotProps">
        <DataTable
            :value="slotProps.data.sample_files"
            :rows="20"
            :rowsPerPageOptions="[20,30,50]">
<!--          <Column header="Sample" field="sample_id" />-->
          <Column header="Path" field="path" />
          <Column header="Add Resource">
            <template #body="{data}">
              <Button
                  label="Add"
                  outlined
                  raised
                  severity="info"
                  @click="addResource(data)"/>
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
  const res = await useFetch(`/api/search/integrations/${queryString.value}`);
  response.value = res.data.value;
}

const sampleSearch = ref([]);
const querySamples = async () => {
  const res = await useFetch(`/api/search/integrations/sample/${queryString.value}`);
  sampleSearch.value = res.data.value;
}

interface SampleDTO {
  id: string;
  name: string;
  integration_source: string;
  multiomics_sample_id: string;
  external_lims_id: string;
  external_project_name: string;
  description: string;
  sample_files: FilePaths[];
}

interface FilePaths {
  sampleId: string;
  path: string;
}

const createSample = async (data) => {
  const dto : SampleDTO = {
    id: null,
    name: data.sample_name,
    external_lims_id: data.sample_id,
    external_project_name: data.project_name,
    description: data.description,
    sample_files: null
  }

  const res = await useFetch('/api/sample/save', {
    method: "POST",
    body: dto
  })
}

const testing = ref();
const addResource = async (data) => {
  const res = await useFetch('/api/omic/save', {
    method: "POST",
    body: {
      "sampleid": "04be3ce1-d672-4807-a276-d5d11681b351",
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