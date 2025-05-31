<template>
  <InputText v-model="queryString" placeholder="Query..." />
  <section class="bordered centered">
    <Button label="Query" @click="query" />
    <DataTable
      :value="response"
      :rows="20"
      :rowsPerPageOptions="[20,30,50]">
      <Column header="Sample" field="sample_id" />
      <Column header="Path" field="path" />
    </DataTable>
  </section>

  <section class="bordered centered">
    <DataTable
        selection-mode="single"
        :value="sampleSearch"
        :rows="20"
        :rowsPerPageOptions="[20,30,50]">
      <Column header="Sample Id" field="sample_id" />
      <Column header="Sample Name" field="sample_name" />
      <Column header="Description" field="sample_description" />
      <Column header="Project Name" field="project_name" />
      <Column header="Create">
        <template #body="{data}">
          <Button size="small" label="Create Sample" @click="createSample(data)" />
        </template>
      </Column>
    </DataTable>
    <Button label="Search External Samples" @click="querySamples" outlined />
  </section>

  <section class="bordered centered">
    <Button label="Test" @click="test" />
    {{ testing }}
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
  external_lims_id: string;
  external_project_name: string;
  description: string;
}

const createSample = async (data) => {
  const dto : SampleDTO = {
    id: null,
    name: data.sample_name,
    external_lims_id: data.sample_id,
    external_project_name: data.project_name,
    description: data.description
  }

  const res = await useFetch('/api/sample/save', {
    method: "POST",
    body: dto
  })
}

const testing = ref();
const test = async () => {
  const res = await useFetch('/api/omic/save', {
    method: "POST",
    body: {
      "sampleid": "1674062",
      "path": "file://net/nwgc/vol1/data/sample.txt",
      "type": "text_File"
    }
  });

  testing.value = res;
}
</script>

<style scoped>

</style>