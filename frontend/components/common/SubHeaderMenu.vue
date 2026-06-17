<template>
  <section class="sub-header-bar">
    <div class="left-slot">
      <slot name="left"/>
    </div>
    <div class="right-slot">
      <slot name="right"/>
    </div>
  </section>
</template>

<script setup lang="ts">
</script>

<style scoped>
.sub-header-bar {
  display: flex;
  justify-content: space-between;
  backdrop-filter: blur(8px);
  padding: 0.5rem 0.75rem;
  width: 100%;
  position: sticky;
  top: 0;
  z-index: 20;
  border-bottom: 1px solid oklch(0.85 0 80 / 0.4);
}

.left-slot {
  display: flex;
  align-items: center;
}

.right-slot {
  display: flex;
  align-items: center;
}

:deep(.p-button, .p-button-outline) {
  background-color: oklch(0.92 0 80) !important;
  border-color: oklch(0.88 0 80) !important;
  color: oklch(0.30 0.01 80) !important;

  box-shadow: 0 1px 2px oklch(0.3 0 0 / 0.12),
  0 2px 4px oklch(0.3 0 0 / 0.08) !important;

  transition: background-color 0.15s ease,
  box-shadow 0.15s ease,
  transform 0.15s ease;
}

/* Hover */
:deep(.p-button:hover, .p-button-outline:hover) {
  background-color: oklch(0.96 0 80) !important;
  border-color: oklch(0.92 0 80) !important;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px oklch(0.30 0 0 / 0.15) !important;
}

/* Active */
:deep(.p-button:active, .p-button-outline:active) {
  transform: translateY(0);
  box-shadow: 0 1px 2px oklch(0.30 0 0 / 0.10) !important;
}

/* Better spacing for slotted elements */
.sub-header-bar :deep(*:last-child) {
  margin-right: 0;
}
</style>