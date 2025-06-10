<template>
  <div class="matrix-container">
    <h2>Dummy Biosample×Assay Matrix</h2>

    <!-- The “Loading…” indicator will display only momentarily -->
    <div v-if="loading" class="loading">
      Loading matrix…
    </div>

    <template v-else>
      <div class="horizontal-label">
        {{ horizontalLabel }}
      </div>

      <div class="flex-main">
        <div class="vertical-label">
          {{ verticalLabel }}
        </div>
        <table  class="matrix-table">
          <thead>
          <tr>
            <th class="sticky-col sticky-header">Biosample ⧗ Assay</th>
            <th
                v-for="(assay, ai) in assays"
                :key="`assay-col-${ai}`"
                class="sticky-header assay-header"
            >
              {{ assay }}
            </th>
          </tr>
          </thead>
          <tbody>
          <tr
              v-for="(biosample, bi) in biosamples"
              :key="`biosample-row-${bi}`"
          >
            <td class="sticky-col biosample-cell">
              {{ biosample }}
            </td>
            <td
                v-for="(assay, ai) in assays"
                :key="`cell-${bi}-${ai}`"
                :class="['matrix-cell', { 'has-count': cellCount(biosample, assay) > 0 }]"
                @click="goToSearch(biosample, assay)"
            >
            <span v-if="cellCount(biosample, assay) > 0">
              {{ cellCount(biosample, assay) }}
            </span>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

    </template>


  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'

// 1) Reactive state
const loading = ref(true)
const assays = ref([])
const biosamples = ref([])
const counts = reactive({})

const horizontalLabel = ref("Horizontal Label");
const verticalLabel = ref("Vertical Label")

// 2) Populate with “dummy” values on mount
onMounted(() => {
  // (A) Dummy assays
  assays.value = [
    'TF ChIP-seq',
    'Histone ChIP-seq',
    'RNA-seq',
    'DNase Hypersensitivity',
    'ATAC-seq'
  ]

  // (B) Dummy biosamples
  biosamples.value = [
    'K562',
    'HepG2',
    'MCF‑7',
    'GM12878',
    'H1-hESC'
  ]

  // (C) Dummy counts per (biosample, assay)
  //    For every biosample, assign an object whose keys are assays
  biosamples.value.forEach((b) => {
    counts[b] = {}
    assays.value.forEach((a) => {
      // Generate a random integer 0–25 for illustration
      counts[b][a] = Math.floor(Math.random() * 26)
    })
  })

  // Done loading
  loading.value = false
})

// Helper: safely return 0 if missing
function cellCount(biosample, assay) {
  return (counts[biosample] && counts[biosample][assay]) || 0
}

// Click handler: here we’ll just show an alert with the dummy coordinates
function goToSearch(biosample, assay) {
  const c = cellCount(biosample, assay)
  if (c > 0) {
    alert(
        `You clicked (${biosample}, ${assay}) → count = ${c} (dummy).`
    )
  }
}
</script>

<style scoped>
.matrix-container {
  margin: 1rem;
  font-family: Arial, sans-serif;
}

.loading {
  font-size: 1.2em;
  color: #555;
}

.matrix-table {
  border-collapse: collapse;
  table-layout: fixed;
}

.matrix-table th,
.matrix-table td {
  border: 1px solid #ccc;
  text-align: center;
  padding: 0.25rem;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

/* First column (biosample names) sticky on horizontal scroll */
.sticky-col {
  position: sticky;
  left: 0;
  background: #f9f9f9;
  z-index: 2;
}

/* Top row sticky on vertical scroll */
.sticky-header {
  position: sticky;
  top: 0;
  background: #f1f1f1;
  z-index: 3;
}

/* Slightly darker header background */
.assay-header {
  background: #e8e8e8;
  font-weight: bold;
  writing-mode: tb-rl;
  transform: rotate(180deg);
}

/* Biosample names in first column */
.biosample-cell {
  text-align: left;
  font-weight: bold;
}

/* Cells with count > 0 get a subtle hover effect */
.matrix-cell {
  cursor: pointer;
  background: #fff;
  transition: background 0.2s;
}

.matrix-cell:hover {
  background: #f0f8ff;
}

.has-count {
  color: #000;
}

.has-count:hover {
  background: #e6f7ff;
}

.flex-main {
  display: flex;
  flex-direction: row;
}

.horizontal-label {
  font-weight: bold;
  padding-left: 1.5em;
  padding-bottom: 0.5em;
}

.vertical-label {
  font-weight: bold;
  margin-bottom: auto;
  margin-right: 0.5em;
  writing-mode: tb-rl;
  transform: rotate(180deg);
}

</style>
