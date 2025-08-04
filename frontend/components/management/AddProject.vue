<template>
   <button
        @click="showDialog = true"
        class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition"
    >
      Add Project
    </button>

    <!-- Dialog -->
    <div
        v-if="showDialog"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
    >
      <div class="bg-white p-6 rounded-2xl shadow-xl w-full max-w-lg relative">
        <button
            @click="showDialog = false"
            class="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
        >
          ✕
        </button>
        <h2 class="text-xl font-semibold mb-4">Add Project</h2>
        <AddManagedTagForm
            :endpoint="endpoint"
            :existing-names="existingNames"
        />
      </div>
    </div>
</template>

<script setup>
import AddManagedTagForm from '~/components/management/AddManagedTagForm.vue'
import { ref } from 'vue'
import { useFetch } from '#app'

const endpoint = '/api/project'
const showDialog = ref(false)

const { data: existingNamesData } = await useFetch('/api/project/names')
const existingNames = existingNamesData.value || []
</script>
