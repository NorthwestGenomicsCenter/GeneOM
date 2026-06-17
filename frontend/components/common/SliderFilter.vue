<template>
  <section class="slider-filter">
    <div>
      <InputText v-model="minValue" placeholder="Min Value" />
      <label>Min: {{fmtNumber(min)}}</label>
    </div>
    <div>
      <InputText v-model="maxValue" placeholder="Max Value" />
      <label>Max: {{fmtNumber(max)}}</label>
    </div>
    <Slider
      v-model="model"
      :min="min"
      :max="max"
      range
      class="slider" />
  </section>
</template>

<script setup lang="ts">
const props = defineProps<{
  data: any[],
  field: string
}>();

const model = defineModel<number[]>();

const min = computed<number>(() => computeExtent(Number.POSITIVE_INFINITY, Math.min))
const max = computed<number>(() => computeExtent(Number.NEGATIVE_INFINITY, Math.max))

const minValue = computed<string | null>({
  get: () => String(model.value?.[0]) ?? null,
  set: (v) => {
    model.value = [parseFloat(v), model.value?.[1] ?? null]
  }
})

const maxValue = computed<string | null>({
  get: () => String(model.value?.[1]) ?? null,
  set: (v) => {
    model.value = [model.value?.[0] ?? null, parseFloat(v)]
  }
})

const computeExtent = (
    extremeValue: number,
    reducer: (a: number, b: number) => number
): number => {
  if (!props.data?.length) return null

  let value = extremeValue

  for (const row of props.data) {
    const v = Number(row[props.field])
    if (Number.isFinite(v)) {
      value = reducer(value, v)
    }
  }

  return value
}

const fmtNumber = (n : number) : string => {
  return n.toFixed(2)
}

watch(
    () => props.data,
    () => {
      if (model.value == null) {
        return model.value = [min.value, max.value]
      }
    },
    { immediate: true }
)

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