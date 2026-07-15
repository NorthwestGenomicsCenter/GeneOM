<template>
  <div>
    <div class="de-container">
      <Header
          title="Unsupported View"
          @close="removeView(componentKey)"
      >
        <slot name="left"></slot>
      </Header>

      <div class="table-wrapper">
        Unsupported view: {{ viewName }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Header from "~/components/common/Header.vue"
import {useSampleViewStore} from "~/composables/useSampleViewStore";

const viewStore = useSampleViewStore();
const {removeView} = viewStore;

const props = defineProps<{
  componentKey: any,
  viewName: string
}>();

</script>

<style scoped>
.de-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 500px; /* ⬅️ This is your minimum height */
  overflow: hidden;
}

.table-wrapper {
  flex: 1 1 40%;
  min-height: 200px;
  overflow: auto;
}

</style>