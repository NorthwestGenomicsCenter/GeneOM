<template>
  <iframe id="iframe_preview" src="https://amp.pharm.mssm.edu/clustergrammer/viz/5734a7399fee36034aeb787e/rc_two_cats.txt" frameborder="0"></iframe>
  <div id="cluster"></div>
<!--  {{ network_data }}-->
</template>

<script setup lang="ts">
import Clustergrammer from 'clustergrammer';

let network_data = ref([])

onMounted(async () => {
  network_data.value = await $fetch("/mult.json")

  // args must contain root of container and the visualization JSON
  var args = {
    'root': '#cluster',
    'network_data': 'network_data.value'
  }

  // Clustergrammer returns a Clustergrammer object in addition to making
  // the visualization
  // var cgm = Clustergrammer(args).bind(window);
  // var cgm = cluster(args)
})

</script>


<style scoped>
#iframe_preview {
  width: 100%;
  height: 40em;
}
</style>