<template>
  <section class="bordered centered">
    <h3>Upload Expression Outlier Data</h3>
    <FileUpload
        mode="basic"
        name="file"
        url="/api/import/expression_outlier"
        accept=".csv"
        :auto="true"
        :maxFileSize="1000000"/>
  </section>
</template>

<script setup lang="ts">

</script>

<style scoped>
.bordered {
  border: rgb(224 224 224) 1px solid;
  border-radius: 0.75em;
  margin-left: auto;
  margin-right: auto;
  margin-bottom: 1em;
  padding: 1em;
  width: 85em;
}

.centered {
  text-align: center;
}
</style>