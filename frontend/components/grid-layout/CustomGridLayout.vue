<template>
  <div ref="container" class="grid-container" @click="onClickAdd">
    <ResizableDraggable
        v-for="item in layout" :key="item.i"
        :item="item" :grid="grid"
        @update="onItemUpdate(item.i, $event)"
    >
      <component :is="getComponent(item.i)" v-bind="getProps(item.i)" />
    </ResizableDraggable>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ResizableDraggable from './ResizableDraggable.vue'

const props = defineProps({
  layout: Array,
  components: Array,
  columns: Number,
  rowHeight: Number
})
const emit = defineEmits(['update:layout', 'add:view'])

const grid = {
  cols: props.columns,
  cellWidth: null,
  rowHeight: props.rowHeight
}
const container = ref(null)

onMounted(() => {
  const ro = new ResizeObserver(([e]) => {
    grid.cellWidth = Math.floor(
        (e.contentRect.width - (grid.cols - 1) * 10) / grid.cols
    )
  })
  ro.observe(container.value)
})

function getComponent(id) {
  return props.components.find(c => c.id === id)?.component || null
}

function getProps(id) {
  return props.components.find(c => c.id === id)?.props || {}
}

function onItemUpdate(id, { x, y, w, h }) {
  const newLayout = props.layout.map(it =>
      it.i === id ? { ...it, x, y, w, h } : it
  )
  emit('update:layout', newLayout)
}

function onClickAdd(e) {
  const { left, top } = container.value.getBoundingClientRect()
  const mouseX = e.clientX - left
  const mouseY = e.clientY - top
  const gx = Math.floor(mouseX / (grid.cellWidth + 10))
  const gy = Math.floor(mouseY / (grid.rowHeight + 10))
  emit('add:view', { x: gx, y: gy, w: 2, h: 2 })
}
</script>


<style>
.grid-container { position: relative; }
</style>
