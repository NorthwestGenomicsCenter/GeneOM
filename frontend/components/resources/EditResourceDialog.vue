<template>
  <form class="vertical-form" @submit.prevent="save">
    <div class="field">
      <label for="name">Name</label>
      <InputText id="name" v-model="editable.resourceName" />
    </div>

    <div class="field">
      <label for="description">Description</label>
      <InputText id="description" v-model="editable.description" />
    </div>

    <div class="field">
      <Button label="Save" type="submit"/>
    </div>
  </form>
</template>

<script setup lang="ts">
const props = defineProps<{
  resource: any,
}>()

const editable = ref<any>();
watch(props.resource, (newVal, oldVal) => {
  if (newVal != undefined) {
    editable.value = JSON.parse(JSON.stringify(newVal))
  }
}, {
  immediate: true
})

const visible = defineModel<boolean>("visible");

const save = async () => {
  const body = {} as EditOmicResourceDTO;
  body.displayName = editable.value.resourceName;
  body.description = editable.value.description;
  body.visualizationComponents = editable.value.technologiesViewers;

  const response = useClientApi(`/omic/edit/${editable.value.resourceId}`, {
    method: 'POST',
    body: body
  })

  visible.value = false;
}
</script>

<style scoped>
.vertical-form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem; /* nice modern spacing */
  width: 30em;
  padding: 1.5em 2em;
}

.field {
  display: flex;
  flex-direction: column;
}

.field label {
  font-size: 0.9rem;
  font-weight: 600;
  color: oklch(0.35 0 80); /* subtle dark text similar to your palette */
  margin-bottom: 0.25rem;
}
</style>