<template>
<!--  <ViewerImportCoordinateFromFile class="ml-1" @uploaded="uploaded"/>-->
  <div>
    <FileUpload
        mode="basic"
        auto
        url="/api/analysis/import/tabular-data"
        @upload="uploaded" />

    <div v-if="data !== null">
  <!--    {{data.columnFilters}}-->
  <!--    <template v-for="(filter, key) in data.columnFilters">-->
  <!--      <p>-->
  <!--        {{key}}: {{filter}}-->
  <!--      </p>-->
  <!--    </template>-->

      {{filters}}
      <DataTable
          :value="data.rawValues"
          stripedRows
          rowHover
          scrollable
          scrollHeight="600px"
          context-menu
          v-model:contextMenuSelection="selectedRow"
          paginator
          v-model:filters="filters"
          filterDisplay="menu"
          :rowsPerPageOptions="[5, 10, 20, 50]"
          :rows="10"
          data-key="nav_genomic_coords"
          @rowContextmenu="onRowContextMenu"
      >
        <template #header>
          <InputText v-model="filters['global'].value" />
        </template>

        <template v-for="header in data.columnHeaders">
          <Column :header="header" :field="header" sortable>
            <template #filter="{ filterModel }" v-if="data.columnFilters.hasOwnProperty(header)">
              <div v-if="data.columnFilters[header].filterType === 'discrete'">
                <MultiSelect
                    v-model="filterModel.value"
                    :options="data.columnFilters[header].values"
                    filter
                    placeholder="Any"/>
              </div>
              <div v-if="data.columnFilters[header].filterType === 'numeric'">
                <CommonMinMaxSliderFilter
                    v-model="filterModel.value"
                    :minValue="data.columnFilters[header].min"
                    :maxValue="data.columnFilters[header].max"
                />
              </div>
            </template>
            <template #body="{data}">
              {{data[header]}}
            </template>
          </Column>
        </template>
      </DataTable>
    </div>
  </div>
  <ContextMenu ref="cm" :model="menuModel" @hide="selectedRow = null" />
</template>

<script setup lang="ts">
import {FilterMatchMode, FilterOperator, FilterService} from "@primevue/core";

defineOptions({
  name: 'GenericTable'
})

const data = ref<ParsedGenomicDataDTO | null>(null);
const uploaded = (event) => {
  data.value = JSON.parse(event.xhr.response);
  updateFilters();
}

const updateFilters = () => {
  const newFilters = {
    global: {value: null, matchMode: FilterMatchMode.CONTAINS },
  };

  for (const [k, v] of Object.entries(data.value.columnFilters)) {
    if (v.filterType === 'discrete') {
      newFilters[k] = {value: null, matchMode: FilterMatchMode.IN }
    } else if (v.filterType === 'numeric') {
      newFilters[k] = {value: [v.min, v.max], matchMode: FilterMatchMode.BETWEEN}
      newFilters[k] = {value: null, matchMode: FilterMatchMode.BETWEEN}
    } else if (v.filterType === 'continuous') {
      newFilters[k] = {value: null, matchMode: FilterMatchMode.CONTAINS}
    }
  }

  filters.value = newFilters;
}

// -- Filters -----------------------------------------------------------------
const filters = ref<any>({
  global: {value: null, matchMode: FilterMatchMode.CONTAINS }
})

// -- Context Menu ------------------------------------------------------------
const cm = ref();

const onRowContextMenu = (event) => {
  cm.value.show(event.originalEvent);
};

const menuModel = ref([
  {label: 'Navigate to in Igv', icon: 'pi pi-fw pi-search', command: () => navigateIgv(selectedRow)},
]);

const navigateIgv = (selectedRow) => {
  const row = selectedRow.value;

  alert(row.nav_genomic_coords)

  // global.navigate(position);
}

const selectedRow = ref();

</script>

<style scoped>

</style>