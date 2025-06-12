<template>
  <div
      class="grid-container"
      @mousemove="onMouseMove"
      @mouseleave="onMouseLeave"
  >
    <GridLayout
        :layout="layout"
        :col-num="12"
        :row-height="30"
        :is-draggable="true"
        :is-resizable="true"
        :vertical-compact="true"
        :margin="[10, 10]"
        @update:layout="onLayoutUpdated"
    >
      <GridItem
          v-for="item in layout"
          :key="item.i"
          :i="item.i"
          :x="item.x"
          :y="item.y"
          :w="item.w"
          :h="item.h"
      >
        <component
            :is="getComponent(item.i)"
            v-bind="getProps(item.i)"
            :layout-props="item"
        />
      </GridItem>
    </GridLayout>

    <!-- Add View Hover Overlay -->
    <div
        v-if="hoveringOverEmpty"
        class="add-view-overlay"
        :style="addViewStyle"
        @click="addViewAtHoverPosition"
    >
      ➕ Add View
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, ref, computed } from 'vue'
import { GridLayout, GridItem } from 'vue3-grid-layout'

const props = defineProps({
  layout: {
    type: Array,
    required: true,
  },
  components: {
    // Array of { id: string, component: Component, props: object }
    type: Array,
    required: true,
  }
})

const emit = defineEmits(['update:layout', 'add:view'])

function onLayoutUpdated(newLayout) {
  emit('update:layout', newLayout)
}

function getComponent(id) {
  const entry = props.components.find(c => c.id === id)
  return entry?.component || null
}

function getProps(id) {
  const entry = props.components.find(c => c.id === id)
  return entry?.props || {}
}

const hoveringOverEmpty = ref(false)
const addViewStyle = ref({ top: '0px', left: '0px' })
const hoverGridPosition = ref({ x: 0, y: 0 })

const GRID_CELL_WIDTH = 100 // Adjust to match your actual grid size
const GRID_CELL_HEIGHT = 30
const MARGIN = 10
const GRID_COLS = 12

function onMouseMove(event: MouseEvent) {
  const gridRect = (event.currentTarget as HTMLElement).getBoundingClientRect()
  const offsetX = event.clientX - gridRect.left
  const offsetY = event.clientY - gridRect.top

  const col = Math.floor(offsetX / (GRID_CELL_WIDTH + MARGIN))
  const row = Math.floor(offsetY / (GRID_CELL_HEIGHT + MARGIN))

  const isCellEmpty = checkIfCellIsEmpty(col, row)

  if (isCellEmpty) {
    hoveringOverEmpty.value = true
    addViewStyle.value = {
      left: `${col * (GRID_CELL_WIDTH + MARGIN)}px`,
      top: `${row * (GRID_CELL_HEIGHT + MARGIN)}px`,
      position: 'absolute',
      zIndex: '10',
      backgroundColor: 'rgba(255,255,255,0.9)',
      border: '1px dashed #ccc',
      padding: '4px',
      cursor: 'pointer'
    }
    hoverGridPosition.value = { x: col, y: row }
  } else {
    hoveringOverEmpty.value = false
  }
}

function onMouseLeave() {
  hoveringOverEmpty.value = false
}

function checkIfCellIsEmpty(x: number, y: number): boolean {
  return !props.layout.some(item => {
    return (
        x >= item.x &&
        x < item.x + item.w &&
        y >= item.y &&
        y < item.y + item.h
    )
  })
}

function addViewAtHoverPosition() {
  emit('add:view', {
    x: hoverGridPosition.value.x,
    y: hoverGridPosition.value.y,
  })
  hoveringOverEmpty.value = false
}
</script>

<style scoped>
/* Prevent text selection while resizing */
:deep(.vue-grid-item.resizing),
:deep(.vue-grid-item.dragging) {
  user-select: none;
  -webkit-user-select: none;
  -ms-user-select: none;
}

.grid-container {
  position: relative;
  width: 100%;
  height: 100%;
}
</style>
