<template>
  <div class="container mx-auto px-4 py-8">
    <div class="flex justify-between items-center mb-8">
      <h1 class="text-3xl font-bold text-gray-900">Technologies</h1>
      <NuxtLink to="/projects" class="text-blue-600 hover:text-blue-800 font-medium">
        ← View by Projects
      </NuxtLink>
    </div>

    <div v-if="status === 'pending'" class="text-center py-8">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
      <p class="mt-4 text-gray-600">Loading technologies...</p>
    </div>

    <div v-else-if="error" class="text-center py-8">
      <p class="text-red-600">Error loading technologies: {{ error }}</p>
    </div>

    <div v-else>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <div
            v-for="technology in technologies"
            :key="technology.id"
            class="bg-white rounded-lg shadow-md border border-gray-200 p-6 hover:shadow-lg transition-shadow cursor-pointer"
            @click="navigateToSamples(null, technology.id)"
        >
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-semibold text-gray-900">{{ technology.name }}</h3>
            <div class="text-2xl">{{ technology.icon || '🔬' }}</div>
          </div>

          <p class="text-gray-600 text-sm mb-4">{{ technology.description }}</p>

          <div class="flex justify-between items-center">
            <span class="text-sm text-gray-500">Total Samples</span>
            <span class="text-xl font-bold bg-blue-100 text-blue-800 px-3 py-1 rounded-full">
              {{ technology.sampleCount }}
            </span>
          </div>
        </div>
      </div>

      <div class="mt-8 bg-gray-50 rounded-lg p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Technology Summary</h3>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-center">
          <div>
            <div class="text-2xl font-bold text-blue-600">{{ technologies?.length || 0 }}</div>
            <div class="text-sm text-gray-600">Total Technologies</div>
          </div>
          <div>
            <div class="text-2xl font-bold text-green-600">{{ totalSamples }}</div>
            <div class="text-sm text-gray-600">Total Samples</div>
          </div>
          <div>
            <div class="text-2xl font-bold text-purple-600">{{ averageSamples }}</div>
            <div class="text-sm text-gray-600">Avg per Technology</div>
          </div>
          <div>
            <div class="text-2xl font-bold text-orange-600">{{ mostUsedTech?.name || 'N/A' }}</div>
            <div class="text-sm text-gray-600">Most Used</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// Fetch technologies data
const { data: technologies, status, error } = await useFetch('/api/technology/all')

// Computed properties for summary stats
const totalSamples = computed(() => {
  return technologies.value?.reduce((sum, tech) => sum + tech.sampleCount, 0) || 0
})

const averageSamples = computed(() => {
  if (!technologies.value?.length) return 0
  return Math.round(totalSamples.value / technologies.value.length)
})

const mostUsedTech = computed(() => {
  if (!technologies.value?.length) return null
  return technologies.value.reduce((max, tech) =>
      tech.sampleCount > (max?.sampleCount || 0) ? tech : max
  )
})

const navigateToSamples = (projectId, technologyId) => {
  const query = new URLSearchParams()
  if (projectId) query.append('project', projectId)
  if (technologyId) query.append('technology', technologyId)
  navigateTo(`/samplelookup?${query.toString()}`)
}

</script>