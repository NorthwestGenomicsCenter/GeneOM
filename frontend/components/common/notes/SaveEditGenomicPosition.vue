<template>
  <section class="pb-8">
    <h3 class="pb-4">
      <span class="font-bold">
        Save the current genomic position:
      </span>
       Chr {{pos.chrom}}: {{pos.start}}-{{pos.end}}
    </h3>
    <h3 class="font-bold pb-2">Position Notes:</h3>
    <Textarea v-model="notes" class="notes-text-area pb-8"/>
  </section>
  <section class="flex justify-end">
    <Button :label="saveLabel" @click="save"/>
  </section>
</template>

<script setup lang="ts">
const global = useGlobalStore();
const {currentGenomicPosition: pos} = storeToRefs(global)

const toast = useToast();

const props = defineProps<{
  analysisId?: string,
  genomicPositionId?: string,
}>();

const visible = defineModel<boolean>("visible");

const notes = ref<string>();

const save = async () => {
  const dto : SaveAnalysisPositionDTO = {
    analysisId: props.analysisId,
    chromosome: pos.value.chrom,
    start: pos.value.start,
    end: pos.value.end,
    notes: notes.value
  }

  try {
    let res = await useClientApi(`/snapshot/analysis-position/${props.analysisId}`, {
      method: "POST",
      body: dto
    })

    visible.value = false;
    toast.add({severity: "success", summary: "Saved", life: 2000})
  } catch (error) {
    toast.add({severity: "error", summary: "Unable to Save", detail: `Unable to save\n${error}`, life: 8000})
  }
}

const saveLabel = computed(() => props.genomicPositionId === undefined ? 'Save' : "Update")

</script>

<style scoped>
.notes-text-area {
  width: 80ch;
  height: 20ch;
}

.text-right {
  margin-left: auto;
}
</style>