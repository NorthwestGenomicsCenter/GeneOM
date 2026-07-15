<template>
  <div class="carousel position-container" :class="{ editing: isEditing }">
    <DataTable
        :value="table"
        class="panel table-panel"
        v-motion slide-in
    >
      <Column header="Chromosome" field="chromosome">
        <template #body="{data}">
          <a @click="setEditing(data)">{{ data.chromosome }}</a>
        </template>
      </Column>
      <Column header="Start" field="start" />
      <Column header="End" field="end" />
      <Column header="Notes" field="notes" />
      <Column header="Navigate">
        <template #body="{data}">
          <Button label="Go" severity="secondary" outlined @click="nav(data)" />
        </template>
      </Column>
      <Column header="">
        <template #body="{data}">
          <Button text label="Edit" severity="secondary" outlined @click="setEditing(data)" />
        </template>
      </Column>
    </DataTable>

    <div class="panel edit-panel">
      <div v-if="editingRow" class="edit-form">
        <SaveEditGenomicPosition :genomic-position-id="editingRow.id" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useMotions, MotionComponent } from '@vueuse/motion'
import SaveEditGenomicPosition from "~/components/common/notes/SaveEditGenomicPosition.vue";

// Get custom controls
const { custom } = useMotions()

const global = useGlobalStore();
const {currentGenomicPosition: pos} = storeToRefs(global)

const props = defineProps<{
  table: any[]
}>();
const visible = defineModel<boolean>("visible");

const nav = (row) => {
  const position : GenomicPosition = {
    chr: row.chromosome,
    start: row.start,
    end: row.end
  }

  global.navigate(position)
  visible.value = false;
}


//-- Edit the saved genomic position
const editing = ref<boolean>(false)
const snapshotPosition = ref()

// const setEditing = (data) => {
//   snapshotPosition.value = data;
//   initialValues.snapshotNotes = data.notes;
//   editing.value = true;
// }

const initialValues = reactive({
  snapshotNotes: ''
});

const resolver = ({ values }) => {
  const errors = {};

  if (!values.username) {
    errors.snapshotNotes = [{ message: 'Notes are required.' }];
  }

  return {
    values, // (Optional) Used to pass current form values to submit event.
    errors
  };
};

const updateSnapshotPosition = async (val) => {
  const pos = snapshotPosition.value;

  const payload : SaveAnalysisPositionDTO = {
    analysisId: pos.analysisId,
    chromosome: pos.chromosome,
    end: pos.end,
    notes: pos.notes,
    start: pos.start
  }

  try {
    const res = await useClientApi(`/snapshot/update/${snapshotPosition.value.id}`, {
      method: 'POST',
      body: payload
    })
  } catch (err) {
    console.log("Unable to update the notes")
  }
}

const deleteSnapshotPosition = async () => {
  try {
    const res = await useClientApi(`/snapshot/delete/${snapshotPosition.value.id}`, {
      method: 'DELETE'
    })
  } catch (err) {
    console.log("Unable to delete the notes")
  }
}

// -- Editing -----------------------------------------------------------------
const isEditing = ref(false)
const editingRow = ref<any>(null)

function setEditing(data: any) {
  editingRow.value = data
  isEditing.value = true
}

</script>

<style scoped>
.notes-text-area {
  width: 80ch;
  height: 20ch;
}

.text-right {
  margin-left: auto;
}

.position-container {
  display: flex;
  min-height: 30rem;
  min-width: 60rem;
}

.w-96 > div {
  display: contents;
}

.w-96 label {
  justify-content: end;
  font-weight: bold;
  white-space: nowrap;
}


.carousel {
  position: relative;
  overflow: hidden;
  display: grid;
}

.panel {
  grid-area: 1 / 1;
  width: 100%;
  transition: transform 0.4s ease;
  will-change: transform;
}

.table-panel {
  transform: translateX(0);
}

.edit-panel {
  transform: translateX(100%);
}

.carousel.editing .table-panel {
  transform: translateX(-100%);
}

.carousel.editing .edit-panel {
  transform: translateX(0);
}
</style>