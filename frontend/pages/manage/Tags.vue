<template>
  <div>
    {{ types }}
  </div>

  <div>
    {{ managedTypesTable }}
  </div>

  <div>
    {{ uberon }}
  </div>

  <div>
    {{ featured }}
    <Button @click="getFeatured" label="Get Featured" />
  </div>

  <div>
    {{ allTissue }}
    <Button @click="getAll" label="Get All" />
  </div>

  <Button label="query" @click="query" />
  <Button label="Create Tag" @click="createTag" />

  <Button @click="importHpJson" label="Import Onotology"/>

  <TissueDropdown v-model="test1"/>{{ test1 }}
  <TissueDropdown v-model="test2"/>{{ test2 }}
  <TissueDropdown v-model="test3"/>{{ test3 }}
  <TissueDropdown v-model="test4"/>{{ test4 }}

  <InputText v-model="searchQuery" @change="search"/>
  {{ searchResults }}

  <TissueTable show-featured-column />

</template>

<script setup lang="ts">
import TissueTable from "@/components/management/TissueTable.vue"
import TissueDropdown from "@/components/common/TissueDropdown.vue"

const test1 = ref();
const test2 = ref();
const test3 = ref();
const test4 = ref();
const searchQuery = ref();
const searchResults = ref();

const search = async () => {
  const res = await useApi(`/tag/ontology/search/${searchQuery.value}`);
  searchResults.value = res.data.value;
}

const types = ref<{ key: string; value: string }[]>([]);
const managedTypesTable = ref<any[]>([]);

// Load types on mount
const res = await useApi("/tag/managed");
types.value = res.data.value;

const uberon = ref("uberon");
const importHpJson = async () => {
  const res = await useApi("/tag/import/ontology");
  uberon.value = res.data.value;
}

const featured = ref("featured tissue");
const getFeatured = async () => {
  const res = await useApi("/tag/ontology/featured");
  featured.value = res.data.value;
}

const allTissue = ref("all tissue");
const getAll = async () => {
  const res = await useApi("/tag/ontology/all");
  allTissue.value = res.data.value;
}

const query = async () => {
  if (!types.value.length) {
    console.warn("Types not yet loaded.");
    return;
  }

  const key = types.value[0].key;
  console.log("Loading for key:", key);

  const res = await useApi("/tag/select/" + key);
  managedTypesTable.value = res.data;
};

const createTag = async () => {
  const dto = {
    type: "Technology",
    text: "Test Type"
  }

  const res = await useApi("/tag/managed/save", {
    method: "POST",
    body: dto
  })

  alert(res.data);
}
</script>

<style scoped>

</style>