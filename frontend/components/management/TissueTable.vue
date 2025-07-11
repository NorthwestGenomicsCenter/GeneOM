<template>
  <div class="p-4">
    <InputText
        v-model="searchText"
        placeholder="Search..."
        class="mb-3 w-full"
        @keyup="updateSearch"
        autofocus
    />
    selected: {{ selected }}
    <DataTable
        v-if="terms != null && terms.length != 0"
        v-model:filters="filters"
        v-model:selection="selected"
        :rows="10"
        :rowsPerPageOptions="[5,10,20]"
        :totalRecords="recordsCount"
        :value="terms"
        lazy
        paginator
        responsiveLayout="scroll"
        rowHover
        selection-mode="single"
        stripedRows
        @page="pageEvent"
        @update:selection="selectionUpdated"
    >
      <Column
        v-if="showSelectColumn"
        selectionMode="single"
        headerStyle="width: 3rem" />
      <Column field="name" header="Name" sortable>
        <template #body="{data}">
          <div class="max-w-3xs line-clamp-1 text-ellipsis">
            {{ data.name }}
          </div>
        </template>
      </Column>
      <Column field="definition" header="Definition">
        <template #body="{data}">
          <div class="max-w-xl line-clamp-1 text-ellipsis">
            {{ data.definition }}
          </div>
        </template>
      </Column>
      <Column
        v-if="showFeaturedColumn"
        header="Featured">
        <template #body="{ data }">
          <Button
              :label="data?.featured ? 'Unset Featured' : 'Set Featured'"
              @click="toggleFeatured(data)"
              size="small"
          />
        </template>
      </Column>
    </DataTable>
    <Toast/>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { FilterMatchMode } from '@primevue/core/api';
import { useToast } from 'primevue/usetoast'
import { useApi } from '~/composables/useApi'

interface Props {
  showFeaturedColumn: boolean,
  showSelectColumn: boolean
}

const props = defineProps<Props>();
const selected = ref();
const emits = defineEmits(['updated'])
const searchText = defineModel("searchText")

const toast = useToast()
const terms = ref<any>([])
const filters = computed(async () => {
  updateSearch();

  return {
    name: { value: searchText.value, matchMode: FilterMatchMode.CONTAINS },
  }
});

const page = ref({
  "originalEvent": {
    "page": 0,
    "first": 0,
    "rows": 10,
    "pageCount": 1
  },
  "first": 0,
  "rows": 10,
  "sortField": null,
  "sortOrder": null,
  "multiSortMeta": [],
  "filters": {
    "name": {
      "value": null,
      "matchMode": "contains"
    }
  },
  "pageCount": 1,
  "page": 0
});

const updateSearch = async() => {
  page.value.filters.name.value = searchText.value
  page.value.first = 0;
  await loadTerms();
}

const pageEvent = async (event) => {
  page.value = event
  await loadTerms();
}
const recordsCount = ref<number>(0);

// Load the terms for the table
const loadTerms = async () => {
  try {
    const res = await useApi('/tag/ontology/pagination', {
      method: 'POST',
      body: page.value
    })
    console.table(res.data.value)
    terms.value = res.data.value.tissues
    recordsCount.value = res.data.value.records
  } catch (error) {
    // toast.add({
    //   severity: 'error',
    //   summary: 'Error',
    //   detail: 'Failed to load ontology terms',
    //   life: 3000,
    // })
  }
}
loadTerms();

// selection updated
const selectionUpdated = () => {
  emits('updated', selected.value)
}

const toggleFeatured = async (term: TissueDTO) => {
  try {
    term.featured = !term.featured;

    await useApi(`/tag/ontology/toggle-featured`, {
      method: 'POST',
      body: term,
    })

    toast.add({
      severity: 'success',
      summary: 'Success',
      detail: `Featured ${term.featured ? 'enabled' : 'disabled'} for ${term.name}`,
      life: 3000,
    })
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: `Failed to toggle featured for ${term.name}`,
      life: 3000,
    })
  }
}
</script>
