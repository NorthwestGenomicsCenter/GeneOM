<template>
  <DataTable
      :value="users"
      dataKey="id"
      selection-mode="multiple"
      v-model:selection="selectedUsers"
      style="width: 30em;"
  >
    <Column selectionMode="multiple" header-style="width: 3em"/>
    <Column header="Name">
      <template #body="{data}">
        {{ data.firstName }} {{ data.lastName }}
      </template>
    </Column>
  </DataTable>
</template>

<script setup lang="ts">

defineOptions({
  name: 'SelectUser'
})

const selectedUsers = defineModel<UserDTO[] | null>('selectedUsers', {
  default: null
})

const {data: users, refresh} = useApi<UserDTO[]>("/users/all")
</script>

<style scoped>

</style>