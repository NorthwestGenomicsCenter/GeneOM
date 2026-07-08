<template>
  <div>
    <Header
        title="Table"
        disableToggle
        @settings="selectDialogVisible = true"
        @close="removeView(componentKey)"
    >
    </Header>

    {{ resources }}
    {{ table }}
    <div v-if="table !== null">

<!--      {{filters}}-->
      <DataTable
          :value="table.rawValues"
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

        <template v-for="header in table.columnHeaders">
          <Column :header="header" :field="header" sortable>
            <template #filter="{ filterModel }" v-if="table.columnFilters.hasOwnProperty(header)">
              <div v-if="table.columnFilters[header].filterType === 'discrete'">
                <MultiSelect
                    v-model="filterModel.value"
                    :options="table.columnFilters[header].values"
                    filter
                    placeholder="Any"/>
              </div>
              <div v-if="table.columnFilters[header].filterType === 'numeric'">
                <CommonMinMaxSliderFilter
                    v-model="filterModel.value"
                    :minValue="table.columnFilters[header].min"
                    :maxValue="table.columnFilters[header].max"
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

  <Dialog header="Select Samples" v-model:visible="selectDialogVisible" modal>
    <ViewerResourceTable
        :componentKey="componentKey"
        :visualizers="['generic-table']"
        v-model:visible="selectDialogVisible"
    />
  </Dialog>


</template>

<script setup lang="ts">
import {FilterMatchMode, FilterOperator, FilterService} from "@primevue/core";
import Header from "~/components/common/Header.vue";
import {useSampleViewStore} from "~/composables/useSampleViewStore";

defineOptions({
  name: 'GenericTable'
})

const props = defineProps<{
  componentKey: any
}>();

const viewStore = useSampleViewStore();
const { removeView } = viewStore;

const table = ref<ParsedGenomicDataDTO | null>(null);

// -- Select Resource for the Table -------------------------------------------
const selectDialogVisible = ref<boolean>(false);
const resourceStore = useViewResourcesStore();
const {selectedResources, registerConfigCallback, unregisterConfigCallback} = resourceStore;
const resources = selectedResources(props.componentKey);

const updateResourceData = async () => {
  table.value = {} as ParsedGenomicDataDTO;
  if (resources.value.length == 0) return

  try {
    table.value = await useClientApi(`/omic/visualize/transform/GenericTableVisualizer/${resources.value[0].resourceId}`);
  } catch (error) {
  //   toast.add({
  //     severity: "error",
  //     summary: "Unable to Retrieve Resource",
  //     detail: `Trying to find resource for Differential Expression Table. \n\n ${error}`,
  //     life: 10_000
  //   })
    console.warn("Unable to access genomic data")
  }
}

watch(resources, updateResourceData, {deep: true, immediate: true})

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