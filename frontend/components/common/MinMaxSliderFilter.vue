=<template>
  <section class="slider-filter" v-if="model !== null">
    <div>
      <InputText v-model="model[0]" placeholder="Min Value" />
      <label>Min: {{fmtNumber(minValue)}}</label>
    </div>
    <div>
      <InputText v-model="model[1]" placeholder="Max Value" />
      <label>Max: {{fmtNumber(maxValue)}}</label>
    </div>
    <Slider
      v-model="model"
      :min="minValue"
      :max="maxValue"
      range
      class="slider" />
  </section>
</template>

<script setup lang="ts">
const props = defineProps<{
  minValue: number,
  maxValue: number
}>();

const model = defineModel<number[] | null>();
onMounted(() => {
  model.value = [null, null]
})

const fmtNumber = (n : number) : string => {
  return n.toFixed(2)
}

</script>

<style scoped>
label {
  padding-left: 0.5em;
}

.slider {
  margin: 1em;
}

.slider-filter {
  padding: 1em;
  width: 30em;
}
</style>