<template>
  <div class="samples bordered">
    <section class="details">
      <FloatLabel variant="on" class="input">
        <InputText id="name" class="w-full" v-model="sample.name"/>
        <label for="name">Name</label>
      </FloatLabel>

      <FloatLabel variant="on" class="input">
        <InputText id="project-name" class="w-full" v-model="sample.projectName"/>
        <label for="project-name">Project Name</label>
      </FloatLabel>

      <FloatLabel variant="on" class="input">
      <TextArea
          id="description"
          v-model="sample.description"
          rows="5" cols="60"
          class="w-full"/>
        <label for="project-name">Description</label>
      </FloatLabel>
    </section>
    <Button label="Save" @click="save" :disabled="saveDisabled"/>
  </div>
  <Toast/>
</template>

<script setup lang="ts">
// Setup the page
const toast = useToast();

const sample = ref<CreateSampleDTO>({} as CreateSampleDTO);

const saveDisabled = computed(() =>
    !sample.value.name?.trim() || !sample.value.projectName?.trim()
);

const emit = defineEmits(["close"]);

// Save Changes
const save = async () => {
  const res = await useApi(`/sample/create`, {
    method: "POST",
    body: sample.value
  })

  if (res.error.value) {
    toast.add({
      severity: "error",
      summary: 'Error',
      detail: `Unable to Save Sample Information`,
      life: 16000
    })
  } else {
    toast.add({
      severity: "success",
      summary: 'Saved',
      detail: `Updated the Sample Information`,
      life: 6000
    })
  }

  emit('close');
}
</script>

<style scoped>
.samples {
  padding: 2rem;
}

.details {
  width: 45%;
}

.input {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
}
</style>