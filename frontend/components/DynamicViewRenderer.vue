<template>
  <div class="w-full h-full overflow-auto p-4 bg-white rounded shadow">
    <template v-if="ViewComponent">
      <ClientOnly fallback-tag="h3" fallback="Loading...">
        <component :is="ViewComponent" :componentKey="dynamicComponentProps.key" v-bind="dynamicComponentProps" />
      </ClientOnly>
    </template>
    <template v-else>
      <Header title="Unsupported view" disableToggle>
        <template #right>
          <em class="pi pi-times-circle" @click="removeView(dynamicComponentProps.key)"/>
        </template>
      </Header>
      <div>Unsupported view: {{ viewName }}</div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { viewComponentMap } from '@/utils/viewComponentRegistry'
import Header from "~/components/common/Header.vue";
import {useSampleViewStore} from "~/composables/useSampleViewStore.js";

const props = defineProps({
  viewName: String,
  componentProps: Object,
})

const dynamicComponentProps = computed(() => {
  console.table(props.componentProps)
  return props.componentProps
})

const ViewComponent = computed(() =>
    viewComponentMap[props.viewName]
)

const viewStore = useSampleViewStore();
const { removeView } = viewStore;
</script>
