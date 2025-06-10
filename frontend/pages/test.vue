<template>
  {{ test }}
  <p>
    {{ auth.expire }}
  </p>
  <Button label="test" @click="c" />
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'default'
})

const test = ref<any>();

const auth = useAuth();

const res = await useFetch("/api/omic/visualizers/386e7662-f127-4090-9963-847516a8ce55")
test.value = res

const c = () => {

}

</script>

<style scoped>
.multiomics-browser {
  font-family: Arial, sans-serif;
  line-height: 1.6;
  color: #333;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  color: #2c3e50;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

h2 {
  color: #3498db;
  margin-top: 20px;
}

ul {
  padding-left: 20px;
}

section {
  margin-bottom: 25px;
}
</style>