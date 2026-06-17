<template>
  <div
    class="overflow-hidden"
    style="width: 75rem; height: 30rem;">
    <MotionComponent is="p" preset="slideVisibleLeft" v-if="!editing">
      <DataTable
          :value="table"
          style="grid-area: 1 / 1"
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
      </DataTable>
    </MotionComponent>

    <MotionComponent
        is="p"
        :enter="{ opacity: 1, y: 0, scale: 1 }"
        :delay="200"
        :duration="1200"
        v-else
    >
      <h1 @click="editing = false">Editing</h1>
      <div class="w-96 pb-5">
        <div>
          <label for="chromosome">Chromosome: </label>
          <span id="chromosome">{{snapshotPosition.chromosome}}</span>
        </div>
        <div>
          <label for="start">Start: </label>
          <span id="start">{{snapshotPosition.start}}</span>
        </div>
        <div>
          <label for="end">End: </label>
          <span id="end">{{snapshotPosition.end}}</span>
        </div>
      </div>
      <Form v-slot="$form" :initialValues :resolver @submit="updateSnapshotPosition">
        <div class="flex flex-col gap-1">
          <Textarea v-model="snapshotPosition.notes" name="snapshotNotes" type="text" placeholder="Notes" fluid rows="7"/>
          <Message v-if="$form.snapshotNotes?.invalid" severity="error" size="small" variant="simple">{{ $form.snapshotNotes.error?.message }}</Message>
        </div>

        <div class="flex justify-end pt-6">
          <Button class="mr-2" :severity="'warn'" icon="pi pi-times" label="Delete" @click="deleteSnapshotPosition"/>
          <Button type="submit" label="Update" />
        </div>
      </Form>
      {{snapshotPosition}}
    </MotionComponent>

  </div>
</template>

<script setup lang="ts">
import { useMotions, MotionComponent } from '@vueuse/motion'

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

const setEditing = (data) => {
  snapshotPosition.value = data;
  initialValues.snapshotNotes = data.notes;
  editing.value = true;
}

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

</script>

<style scoped>
.notes-text-area {
  width: 80ch;
  height: 20ch;
}

.text-right {
  margin-left: auto;
}

.w-96 {
  display: grid;
  grid-template-columns: max-content 1fr;
  row-gap: 4px;
  column-gap: 8px;
}

.w-96 > div {
  display: contents;
}

.w-96 label {
  justify-content: end;
  font-weight: bold;
  white-space: nowrap;
}

</style>