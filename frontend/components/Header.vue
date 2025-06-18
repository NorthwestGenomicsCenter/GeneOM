<template>
  <header class="header">
    <div class="header-container">
      <div class="header-left title">
        UW Multiomics Browser
      </div>
      <div class="header-center">
        <Search/>
      </div>
      <div class="header-right">
        Login
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">

</script>

<style scoped>
.header {
  position: fixed;
  inset: 0 0 auto 0;
  background-color: #4b2e83;
  color: white;
  display: flex;
  vertical-align: center;
  padding: 1rem;
  background-image: url("dna3.png"), linear-gradient(to right, #4b2e83, #4b2e83, #4b2e83, #4b2e83, #4b2e83, #4b2e83, #ba71ff, #ff71b8);
  background-position: top right;
  background-repeat: no-repeat;
  width: 100%;
  z-index: 20;
  height: var(--header-height);
}

.title {
  display: block;
  font-size: 1.5em;
  margin-top: auto;
  margin-bottom: auto;
  font-weight: bold;
  user-select: none;
}

.ui-user {
  display: block;
  font-size: 1.0em;
  margin-top: auto;
  margin-bottom: auto;
  margin-right: 2em;
}

.header-container {
  width: 100%;
  display: flex;
  align-items: center; /* Aligns items vertically in the center */
  justify-content: space-between; /* Distributes items with space between them */
}

.header-left, .header-center, .header-right {
  flex: 1; /* Optional: ensures equal spacing for flexibility */
  text-align: center;
}

.header-center {
  width: 60em;
}

:deep(.header-center input) {
  width: 60em;
}

.header-left {
  text-align: left;
}

.header-right {
  text-align: right;
}
</style>