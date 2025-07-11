<template>
  <div class="container mx-auto px-4 py-8">
    <div class="flex justify-between items-center mb-8">
      <h1 class="text-3xl font-bold text-gray-900">Projects</h1>
      <NuxtLink to="/technologies" class="text-blue-600 hover:text-blue-800 font-medium">
        View by Technologies →
      </NuxtLink>
    </div>

    <div v-if="status === 'pending'" class="text-center py-8">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
      <p class="mt-4 text-gray-600">Loading projects...</p>
    </div>

    <div v-else-if="error" class="text-center py-8">
      <p class="text-red-600">Error loading projects: {{ error }}</p>
    </div>

    <div v-else class="space-y-4">
      <div
          v-for="project in projects"
          :key="project.id"
          class="bg-white rounded-lg shadow-md border border-gray-200 overflow-hidden"
      >
        <div
            class="p-6 cursor-pointer hover:bg-gray-50 transition-colors"
            @click="toggleProject(project.id)"
        >
          <div class="flex justify-between items-center">
            <div>
              <h3 class="text-xl font-semibold text-gray-900">{{ project.name }}</h3>
              <p class="text-gray-600 mt-1">{{ project.description }}</p>
            </div>
            <div class="flex items-center space-x-2">
              <span class="text-sm text-gray-500">{{ project.sampleCount }} samples</span>
              <svg
                  class="w-5 h-5 text-gray-400 transition-transform"
                  :class="{ 'rotate-180': expandedProject === project.id }"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
              >
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </div>
          </div>
        </div>

        <div
            v-if="expandedProjects.has(project.id)"
            class="px-6 pb-6 border-t border-gray-100 bg-gray-50"
        >
          <h4 class="font-medium text-gray-900 mb-3 pt-4">Technologies Used:</h4>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
            <div
                v-for="tech in technologiesMap[project.id]"
                :key="tech.id"
                class="bg-white rounded-lg p-3 shadow-sm border border-gray-200 hover:shadow-md transition-shadow cursor-pointer"
                @click="navigateToSamples(project.id, tech.id)"
            >
              <div class="flex justify-between items-center">
                <span class="font-medium text-gray-900">{{ tech.name }}</span>
                <span class="text-sm bg-blue-100 text-blue-800 px-2 py-1 rounded-full">
                  {{ tech.sampleCount }} samples
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const expandedProjects = ref(new Set())

// Fetch projects data
const { data: projects, status, error } = await useFetch('/api/project/all')
const { data: technologiesMap } = await useFetch('/api/project/all/technologies')

const toggleProject = (projectId) => {
  if (expandedProjects.value.has(projectId)) {
    expandedProjects.value.delete(projectId)
  } else {
    expandedProjects.value.add(projectId)
  }
  // Vue doesn't track Set mutations directly; trigger reactivity:
  expandedProjects.value = new Set(expandedProjects.value)
}

const navigateToSamples = (projectId, technologyId) => {
  navigateTo(`/samplelookup?project=${projectId}&technology=${technologyId}`)
}
</script>