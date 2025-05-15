<template>
  <div class="header-container">
    <h3 class="centered-title" v-html="props.title"></h3>
    <div v-if="!disableToggle" class="right-element">
      Global Filters:&nbsp;&nbsp;<ToggleSwitch v-model="modelValue"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import ToggleSwitch from 'primevue/toggleswitch'

defineOptions({
  name: 'Header'
})

const props = defineProps({
    title: {
      type: String,
      default: 'Untitled'
    },
    disableToggle: {
      type: Boolean,
      default: false
    }
  }
)
const modelValue = defineModel<boolean>();
</script>

<style scoped>
.header-container {
  align-items: center;
  display: flex;
  padding-bottom: 1rem;
  position: relative;
}

.centered-title {
  left: 50%;
  margin: 0;
  position: absolute;
  transform: translateX(-50%);
}

.right-element {
  align-items: center; /* Vertically centers the toggle */
  display: flex;
  height: 100%;
  margin-left: auto;
}
</style>