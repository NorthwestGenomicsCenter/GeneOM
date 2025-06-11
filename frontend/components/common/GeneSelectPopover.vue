<template>
  <Popover ref="op">
    <div class="flex flex-col gap-4 w-[25rem]">
      <div>
        <span class="font-medium block mb-2">Gene</span>
        <InputGroup>
          <Select
              v-model="selectedGene"
              placeholder="Select Gene..."
              :options="props.genes" />
        </InputGroup>
      </div>
      <div style="display: flex; flex-direction: column">
        <Button
            label="Add to Global Filters"
            severity="secondary"
            raised
            rounded
            outlined
            @click="addGene"/>
      </div>
    </div>
  </Popover>
</template>

<script setup lang="ts">
import {useGlobalStore} from '@/composables/useGlobal'

import Popover from "primevue/popover";

const store = useGlobalStore();
const op = ref();

defineOptions({
  name: "GeneSelectPopover",
})

const toggle = (event, el) => {
  op.value.toggle(event, el);
}

defineExpose({
  toggle
});

const props = defineProps({
  genes: {
    type: Array<string>,
    default: []
  }
})

const selectedGene = defineModel<string>();

const addGene = () => {
  store.addGene(selectedGene.value)
}

</script>


<style scoped>

</style>