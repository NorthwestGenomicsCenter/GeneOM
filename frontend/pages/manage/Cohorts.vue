<template>
  <div class="samples bordered">
    <div class="header">
      <h2>Cohorts</h2>
    </div>

    <DataTable
        :value="cohorts">
      <Column header="Name" field="name" />
      <Column header="Description" field="description" />
      <Column header="Number of Samples" field="count" />
    </DataTable>
  </div>

  <div class="bordered">
    <InputText v-model="newCohort.name" placeholder="name" />
    <InputText v-model="newCohort.description" placeholder="description" />
    <Button label="Create" @click="createNew" />
  </div>

  <Toast />

</template>

<script setup lang="ts">

const toast = useToast();

interface Cohort {
  id: string;
  name: string;
  description: string;
  samples: number;
}

const cohorts = ref([])

const query = async () => {
  const res = await useFetch("/api/cohort/all", {
    method: "GET"
  });
  cohorts.value = res.data.value;
}

const res = await useFetch("/api/cohort/all", {
  method: "GET"
});
cohorts.value = res.data.value;

const newCohort = ref<Cohort>({} as Cohort);
const createNew = async () => {
  const res = useFetch(`/api/cohort/create`, {
    method: "POST",
    body: JSON.stringify(newCohort.value),
  });

  console.log(res)

  if (!res.error.value) {
    toast.add({ severity: 'success', summary: 'Info', detail: 'Added Cohort', life: 8000 })
  } else {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Error Adding Cohort', life: 8000 })
  }

  await query();
}
</script>

<style scoped>

</style>