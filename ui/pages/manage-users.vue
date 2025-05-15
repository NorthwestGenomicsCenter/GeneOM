<template>
  <div class="user-management-container">
    <div class="header">
      <h2>User Accounts</h2>
      <Button label="Add User" icon="pi pi-plus" @click="showAddDialog" />
    </div>

    <DataTable
        :value="users"
        stripedRows
        removableSort
        paginator
        :rows="10"
        :rowsPerPageOptions="[5,10,20]"
        :filters="filters"
        v-model:selection="selectedUser"
        sort-field="lastName"
        :sort-order="1"
        dataKey="id"
        class="p-datatable-sm"
    >
      <template #header>
        <div class="table-header">
          <span class="p-input-icon-left">
            <i class="pi pi-search" />
            <InputText v-model="filters.global.value" placeholder="Search users..." />
          </span>
        </div>
      </template>

      <Column field="firstName" header="First Name" sortable></Column>
      <Column field="lastName" header="Last Name" sortable></Column>
      <Column field="email" header="Email" sortable></Column>
      <Column field="userName" header="Username" sortable></Column>
      <Column header="Permissions" style="min-width: 200px">
        <template #body="{data}">
          <Tag v-for="perm in data.permissions" :key="perm" :label="perm" class="mr-2 mb-2">
            {{perm}}
          </Tag>
        </template>
      </Column>
      <Column :exportable="false" style="width: 120px">
        <template #body="{data}">
          <Button icon="pi pi-pencil" class="p-button-rounded p-button-text p-button-sm mr-1" @click="editUser(data)" />
          <Button icon="pi pi-trash" class="p-button-rounded p-button-text p-button-sm p-button-danger" @click="confirmDelete(data)" />
        </template>
      </Column>
    </DataTable>

    <!-- Add/Edit Dialog -->
    <Dialog v-model:visible="userDialog" :header="dialogTitle" :modal="true" class="p-fluid" style="width: 450px">
      <div class="field">
        <label for="firstName">First Name</label>
        <InputText id="firstName" v-model.trim="user.firstName" required="true" autofocus />
      </div>
      <div class="field">
        <label for="lastName">Last Name</label>
        <InputText id="lastName" v-model.trim="user.lastName" required="true" />
      </div>
      <div class="field">
        <label for="email">Email</label>
        <InputText id="email" v-model.trim="user.email" required="true" />
      </div>
      <div class="field">
        <label for="userName">Username</label>
        <InputText id="userName" v-model.trim="user.userName" required="true" />
      </div>
      <div class="field">
        <label>Permissions</label>
        <MultiSelect
            v-model="user.permissions"
            :options="availablePermissions"
            optionLabel="name"
            placeholder="Select permissions"
            display="chip"
        />
      </div>

      <template #footer>
        <Button label="Cancel" icon="pi pi-times" @click="hideDialog" class="p-button-text"/>
        <Button label="Save" icon="pi pi-check" @click="saveUser" autofocus />
      </template>
    </Dialog>

    <!-- Delete Confirmation -->
    <Dialog v-model:visible="deleteDialog" header="Confirm" :modal="true" style="width: 350px">
      <div class="confirmation-content">
        <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem" />
        <span>Are you sure you want to delete <b>{{ userToDelete?.firstName }} {{ userToDelete?.lastName }}</b>?</span>
      </div>
      <template #footer>
        <Button label="No" icon="pi pi-times" @click="deleteDialog = false" class="p-button-text"/>
        <Button label="Yes" icon="pi pi-check" @click="deleteUser" class="p-button-danger" autofocus />
      </template>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {FilterMatchMode} from "@primevue/core";

definePageMeta({
  layout: 'default'
})

interface User {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  userName: string;
  permissions: string[];
}

const users = ref<User[]>([]);
const userDialog = ref(false);
const deleteDialog = ref(false);
const userToDelete = ref<User | null>(null);
const user = ref<User>({} as User);
const selectedUser = ref<User>({} as User);
const submitted = ref(false);
const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS }
});

// Sample permissions - replace with your actual permissions
const availablePermissions = ref([
  {name: 'Admin', value: 'admin'},
  {name: 'Editor', value: 'editor'},
  {name: 'Viewer', value: 'viewer'},
  {name: 'Reports', value: 'reports'},
]);

onMounted(() => {
  // Load initial data - replace with your API call
  loadUsers();
});

const loadUsers = async () => {
  users.value = await $fetch("/api/users/all");
};

const showAddDialog = () => {
  user.value = {} as User;
  submitted.value = false;
  userDialog.value = true;
};

const editUser = (userData: User) => {
  user.value = { ...userData };
  userDialog.value = true;
};

const hideDialog = () => {
  userDialog.value = false;
  submitted.value = false;
};

const saveUser = () => {
  submitted.value = true;

  // Basic validation
  if (user.value.firstName?.trim() &&
      user.value.lastName?.trim() &&
      user.value.email?.trim() &&
      user.value.userName?.trim()) {

    if (user.value.id) {
      // Update existing user
      const index = users.value.findIndex(u => u.id === user.value.id);
      if (index !== -1) {
        users.value[index] = { ...user.value };
      }
    } else {
      // Add new user
      user.value.id = users.value.length ? Math.max(...users.value.map(u => u.id || 0)) + 1 : 1;
      users.value.push({ ...user.value });
    }

    userDialog.value = false;
    user.value = {} as User;
  }
};

const confirmDelete = (userData: User) => {
  userToDelete.value = userData;
  deleteDialog.value = true;
};

const deleteUser = () => {
  if (userToDelete.value?.id) {
    users.value = users.value.filter(u => u.id !== userToDelete.value?.id);
    deleteDialog.value = false;
    userToDelete.value = null;
  }
};

const dialogTitle = computed(() => {
  return user.value.id ? 'Edit User' : 'Add New User';
});
</script>

<style scoped>
.user-management-container {
  padding: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.table-header {
  display: flex;
  justify-content: flex-end;
}

.confirmation-content {
  display: flex;
  align-items: center;
}

.p-chip {
  background-color: var(--primary-color);
  color: white;
}

:deep(.p-multiselect-token) {
  background-color: var(--primary-color);
  color: white;
}
</style>