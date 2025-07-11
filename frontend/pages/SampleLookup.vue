<template>
  <div class="container mx-auto px-4 py-8">
    <div class="mb-8">
      <div class="flex items-center space-x-2 text-sm text-gray-600 mb-4">
        <NuxtLink to="/projects" class="hover:text-blue-600">Projects</NuxtLink>
        <span>/</span>
        <NuxtLink to="/technologies" class="hover:text-blue-600">Technologies</NuxtLink>
        <span>/</span>
        <span class="text-gray-900">Sample Selection</span>
      </div>

      <h1 class="text-3xl font-bold text-gray-900 mb-4">Sample Selection</h1>

      <!-- Filter summary -->
      <div class="bg-blue-50 rounded-lg p-4 mb-6">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-4">
            <span class="text-sm font-medium text-gray-700">Filters:</span>
            <template v-if="selectedProjects.length">
        <span
            v-for="project in selectedProjects"
            :key="project.id"
            class="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm"
        >
          Project: {{ project.name }}
        </span>
            </template>
            <template v-if="selectedTechnologies.length">
        <span
            v-for="tech in selectedTechnologies"
            :key="tech.id"
            class="bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm"
        >
          Technology: {{ tech.name }}
        </span>
            </template>
            <span
                v-if="!selectedProjects.length && !selectedTechnologies.length"
                class="text-gray-500 text-sm"
            >
        No filters applied
      </span>
          </div>
          <button
              v-if="selectedProjects.length || selectedTechnologies.length"
              @click="clearFilters"
              class="text-sm text-blue-600 hover:text-blue-800 font-medium"
          >
            Clear Filters
          </button>
        </div>
      </div>

    </div>

    <div v-if="status === 'pending'" class="text-center py-8">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
      <p class="mt-4 text-gray-600">Loading samples...</p>
    </div>

    <div v-else-if="error" class="text-center py-8">
      <p class="text-red-600">Error loading samples: {{ error }}</p>
    </div>

    <div v-else>
      <!-- Selection controls -->
      <div class="flex items-center justify-between mb-6">
        <div class="flex items-center space-x-4">
          <span class="text-sm text-gray-600">
            {{ samples?.length || 0 }} samples found
          </span>
          <span v-if="selectedSamples.length > 0" class="text-sm font-medium text-blue-600">
            {{ selectedSamples.length }} selected
          </span>
        </div>

        <div class="flex items-center space-x-3">
          <button
              v-if="samples?.length > 0"
              @click="toggleSelectAll"
              class="text-sm text-gray-600 hover:text-gray-800"
          >
            {{ allSelected ? 'Deselect All' : 'Select All' }}
          </button>

          <button
              :disabled="selectedSamples.length === 0"
              @click="proceedToAnalysis"
              class="bg-blue-600 text-white px-6 py-2 rounded-lg font-medium hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
          >
            Proceed to Analysis ({{ selectedSamples.length }})
          </button>
        </div>
      </div>

      <!-- Samples grid -->
      <div v-if="samples?.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
            v-for="sample in samples"
            :key="sample.id"
            class="bg-white rounded-lg shadow-md border border-gray-200 overflow-hidden hover:shadow-lg transition-shadow"
            :class="{ 'ring-2 ring-blue-500': selectedSamples.includes(sample.id) }"
        >
          <div class="p-6">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-semibold text-gray-900">{{ sample.name }}</h3>
              <input
                  type="checkbox"
                  :checked="selectedSamples.includes(sample.id)"
                  @change="toggleSample(sample.id)"
                  class="w-5 h-5 text-blue-600 rounded focus:ring-blue-500"
              >
            </div>

            <div class="space-y-2 text-sm">
              <div class="flex justify-between">
                <span class="text-gray-600">Sample ID:</span>
                <span class="font-mono text-gray-900">{{ sample.id }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">Created:</span>
                <span class="text-gray-900">{{ formatDate(sample.createdAt) }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">Status:</span>
                <span
                    class="px-2 py-1 rounded-full text-xs font-medium"
                    :class="getStatusClass(sample.status)"
                >
                  {{ sample.status }}
                </span>
              </div>
            </div>

            <!-- Projects -->
            <div class="mt-4">
              <span class="text-xs font-medium text-gray-600 block mb-2">Projects:</span>
              <div class="flex flex-wrap gap-1">
                <span
                    v-for="project in sample.projects"
                    :key="project.id"
                    class="bg-blue-100 text-blue-800 px-2 py-1 rounded text-xs"
                >
                  {{ project.name }}
                </span>
              </div>
            </div>

            <!-- Technologies -->
            <div class="mt-3">
              <span class="text-xs font-medium text-gray-600 block mb-2">Technologies:</span>
              <div class="flex flex-wrap gap-1">
                <span
                    v-for="tech in sample.technologies"
                    :key="tech.id"
                    class="bg-green-100 text-green-800 px-2 py-1 rounded text-xs"
                >
                  {{ tech.name }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="text-center py-12">
        <div class="text-gray-400 text-6xl mb-4">🔬</div>
        <h3 class="text-lg font-medium text-gray-900 mb-2">No samples found</h3>
        <p class="text-gray-600">Try adjusting your filters or check back later.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
const route = useRoute()
const selectedSamples = ref([])

// Selected project and technology IDs (as Sets)
const selectedProjectIds = ref(new Set())
const selectedTechnologyIds = ref(new Set())

// Parse query params into sets
const projectId = route.query.project
const technologyId = route.query.technology
if (projectId) selectedProjectIds.value.add(projectId)
if (technologyId) selectedTechnologyIds.value.add(technologyId)

// Fetch metadata for selected projects and technologies (for labels)
const { data: allProjects } = await useFetch('/api/project/all')
const { data: allTechnologies } = await useFetch('/api/technology/all')

// Fetch samples
const query = new URLSearchParams()
for (const id of selectedProjectIds.value) query.append('projectIds', id)
for (const id of selectedTechnologyIds.value) query.append('technologyIds', id)
const { data: samples, status, error } = await useFetch(`/api/sample/all`)

// Computed project and technology objects for display
const selectedProjects = computed(() =>
    allProjects.value?.filter(p => selectedProjectIds.value.has(p.id)) || []
)
const selectedTechnologies = computed(() =>
    allTechnologies.value?.filter(t => selectedTechnologyIds.value.has(t.id)) || []
)

const allSelected = computed(() =>
    samples.value?.length > 0 && selectedSamples.value.length === samples.value.length
)

const toggleSample = (sampleId) => {
  const index = selectedSamples.value.indexOf(sampleId)
  if (index > -1) {
    selectedSamples.value.splice(index, 1)
  } else {
    selectedSamples.value.push(sampleId)
  }
}

const toggleSelectAll = () => {
  if (allSelected.value) {
    selectedSamples.value = []
  } else {
    selectedSamples.value = samples.value.map(sample => sample.id)
  }
}

const clearFilters = () => {
  selectedProjectIds.value.clear()
  selectedTechnologyIds.value.clear()
  navigateTo('/samplelookup')
}

const proceedToAnalysis = () => {
  const sampleIds = selectedSamples.value.join(',')
  navigateTo(`/analysis?samples=${sampleIds}`)
}

const formatDate = (dateString) => new Date(dateString).toLocaleDateString()

const getStatusClass = (status) => {
  const classes = {
    active: 'bg-green-100 text-green-800',
    pending: 'bg-yellow-100 text-yellow-800',
    completed: 'bg-blue-100 text-blue-800',
    failed: 'bg-red-100 text-red-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}
</script>