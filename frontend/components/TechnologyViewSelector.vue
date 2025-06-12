<template>
  <div>
    <button
        @click="showDialog = true"
        class="p-button"
    >
      + Add View
    </button>


    <div v-if="showDialog" class="overlay">
      <div class="dialog">
        <h2>Select a View</h2>
        <input
            type="text"
            v-model="searchQuery"
            placeholder="Search views..."
        />

        <div class="view-list">
          <div
              v-for="(option, index) in filteredOptions"
              :key="index"
              class="view-option"
              :class="{ selected: selectedOption === option }"
              @click="selectedOption = option"
          >
            {{ option.technology }} - {{ option.viewName }}
          </div>
        </div>

        <div class="dialog-buttons">
          <button @click="addSelectedView" :disabled="!selectedOption">Add</button>
          <button @click="closeDialog">Cancel</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const props = defineProps({
  techViewOptions: {
    type: Array,
    required: true,
  },
})
const emit = defineEmits(['add-view'])

const showDialog = ref(false)
const searchQuery = ref('')
const selectedOption = ref(null)

const filteredOptions = computed(() => {
  return props.techViewOptions.filter((option) =>
      `${option.technology} ${option.viewName}`
          .toLowerCase()
          .includes(searchQuery.value.toLowerCase())
  )
})

function addSelectedView() {
  if (selectedOption.value) {
    emit('add-view', selectedOption.value)
    closeDialog()
  }
}

function closeDialog() {
  showDialog.value = false
  selectedOption.value = null
  searchQuery.value = ''
}
</script>

<style scoped>
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: white;
  padding: 20px;
  border-radius: 12px;
  width: 400px;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.view-list {
  flex: 1;
  overflow-y: auto;
  border: 1px solid #ccc;
  margin-top: 10px;
  margin-bottom: 10px;
  max-height: 300px;
}

.view-option {
  padding: 8px;
  cursor: pointer;
}

.view-option:hover,
.view-option.selected {
  background-color: #f0f0f0;
}

.dialog-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}
</style>
