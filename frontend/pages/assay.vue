<template>
  <FiltersBar/>
  <div>
    <section class="bordered centered">
      <DynamicViewRenderer viewName="differential-expression"/>
    </section>
    <section class="bordered centered">
      <DifferentialPeak/>
    </section>

    <!--      <section class="bordered">-->
    <!--        <h3 class="centered">Cluster</h3>-->
    <!--        <ClientOnly fallback-tag="h3" fallback="Loading Cluster">-->
    <!--          <ClusterGrammer />-->
    <!--        </ClientOnly>-->
    <!--      </section>-->
    <section class="bordered">
      <ClientOnly fallback-tag="h3" fallback="Loading IGV...">
        <Igv :grc38="true"/>
      </ClientOnly>
    </section>
  </div>
</template>

<script setup lang="ts">
import FiltersBar from "~/components/common/FiltersBar.vue";

definePageMeta({
  layout: 'default'
})
</script>

<style scoped>

:deep(.centered>canvas) {
  margin-left: auto;
  margin-right: auto;
}
</style>