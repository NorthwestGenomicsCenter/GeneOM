<template>
  <AutoComplete
      v-model="autocompleteValue"
      placeholder="Search samples, genes, variants..."
      class="search-input"
      scroll-height="50rem"
      variant="outlined"
      option-label="name"
      optionGroupLabel="label"
      optionGroupChildren="items"
      :suggestions="autocompleteSuggestions"
      @complete="search"
      @item-select="select">
    <template #optiongroup="{option}">
      <h3 id="search-group-header">{{ option.label }}</h3>
    </template>
    <template #option="{option}">
      <div v-if="option['ensemblGeneId'] !== undefined" class="flex items-center">
        <div>
          Gene:
          <Tag>{{ option.symbol }}</Tag>
          Ensembl:
          <Tag severity="secondary">{{ option.ensemblGeneId }}</Tag>
        </div>
        <div>{{ option.name }}</div>
      </div>
      <div v-else-if="option['externalLimsId'] !== undefined" class="flex items-center">
        <div>
          Sample:
          <Tag severity="info">{{ option.externalLimsId }}</Tag>
          Name:
          <Tag severity="secondary">{{ option.name }}</Tag>
        </div>
      </div>
      <div v-else>
        {{ option.name }}
      </div>
    </template>
  </AutoComplete>
</template>

<script setup lang="ts">
// AutoComplete Search
const autocompleteValue = ref("");
const autocompleteSuggestions = ref([]);

const search = async (event) => {
  autocompleteSuggestions.value = await $fetch(`/api/search/${event.query}`)
}

// Global State
let globalStore = useGlobalStore();

const select = (event) => {
  if (event?.value?.symbol !== undefined) {
    globalStore.$state.gene = event?.value?.symbol;
    globalStore.addGene(event?.value?.symbol)
    autocompleteValue.value = "";
  }
}
</script>


<style scoped>

</style>