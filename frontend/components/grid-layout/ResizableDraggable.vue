<template>
  <div ref="el"
       :style="style"
       class="draggable-item">
    <slot />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import interact from 'interactjs'

const props = defineProps({
  item: Object,
  grid: Object
})
const emit = defineEmits(['update'])
const el = ref(null)
const pos = ref({ x: props.item.x * (props.grid.cellWidth + 10), y: props.item.y * (props.grid.rowHeight + 10), w: props.item.w * props.grid.cellWidth, h: props.item.h * props.grid.rowHeight })

watch(() => props.grid.cellWidth, () => { /* handle initial layout */ })

const style = computed(() => ({
  position: 'absolute',
  transform: `translate(${pos.value.x}px,${pos.value.y}px)`,
  width: pos.value.w + 'px',
  height: pos.value.h + 'px'
}))

onMounted(() => {
  const snapGrid = interact.snappers.grid({ x: props.grid.cellWidth + 10, y: props.grid.rowHeight + 10 })
  interact(el.value)
      .draggable({
        modifiers: [ interact.modifiers.snap({ targets: [snapGrid], range: Infinity, relativePoints: [{x:0,y:0}], offset: 'self' }) ],
        listeners: { end (e) {
            pos.value.x += e.dx; pos.value.y += e.dy
            emit('update', toGrid())
          }}
      })
      .resizable({
        edges: { left:true, right:true, top:true, bottom:true },
        modifiers: [
          interact.modifiers.snapSize({ targets: [interact.snappers.grid({ width: props.grid.cellWidth+10, height: props.grid.rowHeight+10 })] }),
          interact.modifiers.restrictSize({ min: { width: props.grid.cellWidth, height: props.grid.rowHeight } })
        ],
        listeners: { move (e) {
            pos.value.w = e.rect.width
            pos.value.h = e.rect.height
            pos.value.x += e.deltaRect.left
            pos.value.y += e.deltaRect.top
          }, end () { emit('update', toGrid()) }}
      })
})

onBeforeUnmount(() => interact(el.value).unset())

function toGrid() {
  const gx = Math.round(pos.value.x / (props.grid.cellWidth + 10))
  const gy = Math.round(pos.value.y / (props.grid.rowHeight + 10))
  const gw = Math.round(pos.value.w / (props.grid.cellWidth + 10))
  const gh = Math.round(pos.value.h / (props.grid.rowHeight + 10))
  return { x: gx, y: gy, w: gw, h: gh }
}
</script>

<style>
.draggable-item { background: #fafafa; border: 1px solid #ddd; box-sizing: border-box; user-select: none; }
</style>
