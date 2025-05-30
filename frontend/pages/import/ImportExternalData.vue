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
    <Button label="Search External Samples" @click="querySamples" />
    <DataTable
        selection-mode="single"
        :value="sampleSearch"
        :rows="20"
        :rowsPerPageOptions="[20,30,50]">
      <Column selectionMode="single" headerStyle="width: 3rem"></Column>
      <Column header="Sample Id" field="sample_id" />
      <Column header="Sample Name" field="sample_name" />
      <Column header="Description" field="sample_description" />
      <Column header="Project Name" field="project_name" />
    </DataTable>
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
.bordered {
  background-color: white;
  border: rgb(224 224 224) 1px solid;
  border-radius: 0.75em;
  margin-left: auto;
  margin-right: auto;
  margin-bottom: 1em;
  padding: 1em;
  min-width: 85em;
}

</style>