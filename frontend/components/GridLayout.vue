<template>
  <div
      class="grid-container"
      ref="containerRef"
      @mousemove="onMouseMove"
      @mouseleave="onMouseLeave"
  >
    <!--    Documentation    -->
    <!--    https://jbaysolutions.github.io/vue-grid-layout/guide/properties.html#griditem   -->
    <GridLayout
        :layout="layout"
        :col-num="computedColNum"
        :row-height="gridCellHeight"
        :is-draggable="true"
        :is-resizable="true"
        :vertical-compact="true"
        :margin="[MARGIN, MARGIN]"
        @layout-updated="onLayoutUpdated"
    >
      <GridItem
          v-for="item in layout"
          :key="item.i"
          :i="item.i"
          :x="item.x"
          :y="item.y"
          :w="item.w"
          :h="item.h"
          dragAllowFrom=".header-draggable"
          @resize="disableTextSelection"
          @resized="enableTextSelection"
          @move="disableTextSelection"
          @moved="enableTextSelection"
      >
        <component
            :is="getComponent(item.i)"
            :componentProps="getProps(item.i)"
            v-bind="getProps(item.i)"
            :key="item.i"
        />
      </GridItem>
    </GridLayout>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, defineProps, defineEmits, onMounted, onBeforeUnmount } from 'vue'
import { GridLayout, GridItem } from 'vue3-grid-layout'

const props = defineProps({
  layout: {
    type: Array,
    required: true
  },
  components: {
    type: Array, // Array of { id: string, component: Component, props: object }
    required: true
  },
  columns: {
    type: Number,
    default: 30
  },
  selectedViews: {
    type: Array,
    default: new Array()
  }
})

const emit = defineEmits(['update:layout', 'add:view'])

const containerRef = ref<HTMLElement | null>(null)
const containerWidth = ref(0)
const containerHeight = ref(0)

const MARGIN = 10
const computedColNum = computed(() => props.columns)

const gridCellWidth = computed(() => {
  return (containerWidth.value - (computedColNum.value - 1) * MARGIN) / computedColNum.value
})

const gridCellHeight = computed(() => 30)

onMounted(() => {
  const observer = new ResizeObserver(entries => {
    for (const entry of entries) {
      containerWidth.value = entry.contentRect.width
      containerHeight.value = entry.contentRect.height
    }
  })
  if (containerRef.value) observer.observe(containerRef.value)
})

function onLayoutUpdated(newLayout) {
  emit('update:layout', newLayout)
}

function getComponent(id: string) {
  return props.components.find(c => c.id === id)?.component || null
}

function getProps(id: string) {
  return props.components.find(c => c.id === id)?.props || null
}

// Hover detection and Add View logic
const hoveringOverEmpty = ref(false)
const addViewStyle = ref({ top: '0px', left: '0px' })
const hoverGridPosition = ref({ x: 0, y: 0 })

function onMouseMove(event: MouseEvent) {
  const gridRect = (event.currentTarget as HTMLElement).getBoundingClientRect()
  const offsetX = event.clientX - gridRect.left
  const offsetY = event.clientY - gridRect.top

  const col = Math.floor(offsetX / (gridCellWidth.value + MARGIN))
  const row = Math.floor(offsetY / (gridCellHeight.value + MARGIN))

  if (checkIfCellIsEmpty(col, row)) {
    hoveringOverEmpty.value = true
    hoverGridPosition.value = { x: col, y: row }
    addViewStyle.value = {
      position: 'absolute',
      left: `${col * (gridCellWidth.value + MARGIN)}px`,
      top: `${row * (gridCellHeight.value + MARGIN)}px`,
      backgroundColor: 'rgba(255,255,255,0.9)',
      border: '1px dashed #ccc',
      padding: '4px',
      cursor: 'pointer',
      zIndex: '10'
    }
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
  const newItem = {
    i: String(Date.now()),
    x: hoverGridPosition.value.x,
    y: hoverGridPosition.value.y,
    w: 2,
    h: 2
  }
  const newLayout = [...props.layout, newItem]
  emit('update:layout', newLayout)
  emit('add:view', newItem)
  hoveringOverEmpty.value = false
}

// Global text selection disabling
function disableTextSelection(i, newX, newY, newHPx, newWPx){
  const msg = "H(px)=" + newHPx + ", W(px)=" + newWPx;
  console.log(msg);
  makeUnselectable(document.body);
}
function enableTextSelection() {
  makeSelectable(document.body);
}

function makeUnselectable(node) {
  if (node.nodeType == 1) {
    node.classList.add("no-select");
  }
  var child = node.firstChild;
  while (child) {
    makeUnselectable(child);
    child = child.nextSibling;
  }
}

function makeSelectable(node) {
  if (node.nodeType == 1) {
    node.classList.remove("no-select");
  }
  var child = node.firstChild;
  while (child) {
    makeSelectable(child);
    child = child.nextSibling;
  }
}
</script>

<style>
.grid-container {
  position: relative;
  width: 100%;
  height: 100%;
}

</style>
