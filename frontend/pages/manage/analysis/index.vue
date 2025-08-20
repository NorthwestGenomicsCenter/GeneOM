<template>
  <div class="samples bordered">
    <div class="header">
      <h2>Analysis Creation</h2>
    </div>

    <DataTable
        :value="analysis">
      <Column header="Name" field="name">
        <template #body="{data}">
          <NuxtLink :to="`/manage/analysis/edit-${data.id}`">
            {{ data.name }}
          </NuxtLink>
        </template>
      </Column>
      <Column header="Description" field="description" />
      <Column header="Number of Samples" field="count" />
      <Column header="Delete">
        <template #body="{data}">
          <em
            class="pi pi-times-circle delete-icon"
            @click="deleteAnalysis(data.id)"/>
        </template>
      </Column>
    </DataTable>
  </div>

  <Button label="Create Analysis" @click="createDialogVisible = true"/>

  <Dialog v-model:visible="createDialogVisible" header="Create Analysis" modal>
    <section class="add-section">
      <InputText v-model="newAnalysis.name" placeholder="name" />
      <InputText v-model="newAnalysis.description" placeholder="description" />
      <Button label="Create" @click="createNew" />
    </section>
  </Dialog>

  <Toast />

</template>

<script setup lang="ts">
import {useClientApi} from "~/composables/useClientApi";

const toast = useToast();
const createDialogVisible = ref<boolean>(false);

const { data: analysis, refresh } = await useApi("/analysis/all", {
  method: "GET"
});

const newAnalysis = ref<CreateAnalysisDTO>({} as CreateAnalysisDTO);
const createNew = async () => {
  try {
    await useClientApi(`/analysis/create`, {
      method: "POST",
      body: JSON.stringify(newAnalysis.value),
    });
    toast.add({ severity: 'success', summary: 'Info', detail: 'Added Cohort', life: 8000 })
  } catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: `Error Adding Cohort ${error}`, life: 8000 })
  }

  await refresh();
  createDialogVisible.value = false;
}

const deleteAnalysis = async (id : string) => {
  try {
    await useClientApi(`/analysis/${id}`, {
      method: "DELETE"
    });
    toast.add({ severity: 'success', summary: 'Info', detail: 'Added Cohort', life: 8000 })
  } catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: `Error Adding Cohort ${error}`, life: 8000 })
  }

  await refresh();
}
</script>

<style scoped>
.add-section {
  display: flex;
  flex-direction: column;
}

.delete-icon {
  color: red;
  font-size: 16px;
  font-weight: bolder;
}
</style>