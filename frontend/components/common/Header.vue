<template>
  <div class="header-container header-draggable">
    <div class="left-element">
      <slot name="left">
        <em class="pi pi-cog" @click="onSettings"/>
      </slot>
    </div>
    <h3 class="centered-title" v-html="props.title"></h3>
    <div class="right-element">
      <template v-if="!disableToggle">
        Global Filters:&nbsp;&nbsp;<ToggleSwitch v-model="modelValue"/>
      </template>
      <slot name="right">
        <em class="pi pi-times-circle" @click="onClose"/>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import ToggleSwitch from 'primevue/toggleswitch'

defineOptions({
  name: 'DefaultHeader'
})

const props = defineProps({
    title: {
      type: String,
      default: 'Untitled'
    },
    disableToggle: {
      type: Boolean,
      default: true
    }
  }
)
const modelValue = defineModel<boolean>();

const emit = defineEmits<{
  (e: 'close'): void,
  (e: 'settings'): void,
}>()

const onClose = () => emit('close')
const onSettings = () => emit('settings')
</script>

<style scoped>
.header-container {
  align-items: center;
  display: flex;
  padding-bottom: 1rem;
  position: relative;
}

.left-element {
  align-items: center;
  cursor: default;
  display: flex;
  height: 100%;
  margin-right: auto;
}

.centered-title {
  left: 50%;
  margin: 0;
  position: absolute;
  transform: translateX(-50%);
  user-select: none;
}

.right-element {
  align-items: center;
  cursor: default;
  display: flex;
  height: 100%;
  margin-left: auto;
}
</style>