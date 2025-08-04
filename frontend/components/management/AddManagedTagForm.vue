<template>
  <form @submit.prevent="submit" class="space-y-6 max-w-md mx-auto p-6 bg-white rounded-2xl shadow-lg">
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">Name</label>
      <input
          v-model="name"
          type="text"
          class="w-full px-4 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          placeholder="Enter name"
          required
      />
    </div>

    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
      <textarea
          v-model="description"
          rows="4"
          class="w-full px-4 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          placeholder="Optional description"
      />
    </div>

    <div v-if="error" class="text-red-600 text-sm font-medium">
      {{ error }}
    </div>

    <div v-if="success" class="text-green-600 text-sm font-medium">
      Created successfully!
    </div>

    <button
        type="submit"
        :disabled="loading"
        class="w-full bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
    >
      {{ loading ? 'Submitting…' : 'Submit' }}
    </button>
  </form>
</template>

<script setup>
import { ref } from 'vue'
import { useFetch } from '#app'

defineProps({
  endpoint: String,
  existingNames: Array,
})

const name = ref('')
const description = ref('')
const error = ref('')
const success = ref(false)
const loading = ref(false)

const submit = async () => {
  error.value = ''
  success.value = false

  if (existingNames && existingNames.includes(name.value.trim())) {
    error.value = 'That name already exists.'
    return
  }

  loading.value = true

  const {data, error: fetchError} = await useFetch(endpoint, {
    method: 'POST',
    body: {
      name: name.value.trim(),
      description: description.value,
    },
  })

  loading.value = false

  if (fetchError.value) {
    error.value = fetchError.value?.data?.message || 'An error occurred.'
  } else {
    success.value = true
    name.value = ''
    description.value = ''
  }
}
</script>
