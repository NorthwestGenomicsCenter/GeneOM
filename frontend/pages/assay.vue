<template>
    <div>
      <section class="bordered centered">
        <DifferentialExpression />
      </section>
      <section class="bordered centered">
        <DifferentialPeak />
      </section>

<!--      <section class="bordered">-->
<!--        <h3 class="centered">Cluster</h3>-->
<!--        <ClientOnly fallback-tag="h3" fallback="Loading Cluster">-->
<!--          <ClusterGrammer />-->
<!--        </ClientOnly>-->
<!--      </section>-->
      <section class="bordered">
        <ClientOnly fallback-tag="h3" fallback="Loading IGV...">
          <Igv :grc38="true" />
        </ClientOnly>
      </section>
    </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'default'
})
</script>

<style scoped>
.bordered {
  background-color: white;
  border: rgb(224 224 224) 1px solid;
  border-radius: 0.75em;
  margin-left: auto;
  margin-right: auto;
  margin-bottom: 1em;
  padding: 1em;
  min-width: 85em;
}

.centered {
  text-align: center;
}

:deep(.centered>canvas) {
  margin-left: auto;
  margin-right: auto;
}
</style>