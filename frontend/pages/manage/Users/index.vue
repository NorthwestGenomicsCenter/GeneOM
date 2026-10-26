<template>
  <div class="bordered user-management-container">
    <div class="header">
      <h1>User Accounts</h1>
    </div>

    <DataTable
        :value="users"
        :filters="filters"
        :rows="10"
        :rowsPerPageOptions="[5,10,20]"
        :sort-order="1"
        :rowClass="rowClass"
        class="p-datatable-sm"
        dataKey="id"
        paginator
        removableSort
        sort-field="lastName"
        stripedRows
        v-model:selection="selectedUser"
    >
      <template #header>
        <IconField iconPosition="left">
          <InputIcon class="pi pi-search" />
          <InputText
            v-model="filters.global.value"
            placeholder="Search users..."
            size="small"
          />
        </IconField>
      </template>

      <Column field="firstName" header="First Name" sortable></Column>
      <Column field="lastName" header="Last Name" sortable></Column>
      <Column field="email" header="Email" sortable></Column>
      <Column field="userName" header="Username" sortable></Column>
      <Column field="enabled" header="Enabled" sortable></Column>
      <Column header="Permissions" style="min-width: 200px">
        <template #body="{data}">
          <Tag v-for="perm in data.permissions" :key="perm" :label="perm" class="mr-2 mb-2">
            {{perm}}
          </Tag>
        </template>
      </Column>
      <Column :exportable="false" style="width: 120px">
        <template #body="{data}">
          <Button
              icon="pi pi-pencil"
              class="p-button-rounded p-button-text p-button-sm mr-1"
              v-tooltip.bottom="{
                value: 'Edit User',
                showDelay: 500,
                hideDelay: 200
              }"
              @click="editUser(data)"
          />
          <Button
              icon="pi pi-user-minus"
              class="p-button-rounded p-button-text p-button-sm p-button-danger"
              v-tooltip.bottom="{
                value: 'Disable User',
                showDelay: 500,
                hideDelay: 200
              }"
              @click="confirmDelete(data)"
          />
        </template>
      </Column>
    </DataTable>

    <div class="footer">
      <Button label="Add User" icon="pi pi-plus" @click="showAddDialog" />
    </div>

    <!-- Add/Edit Dialog -->
    <Dialog
      v-model:visible="createUserDialogVisible"
      :header="dialogTitle"
      :modal="true"
      class="p-fluid"
      style="width: 450px"
    >
      <FloatLabel variant="over" class="field">
        <label for="firstName">First Name</label>
        <InputText id="firstName" v-model.trim="createUser.firstName" required="true" autofocus />
      </FloatLabel>
      <FloatLabel variant="over" class="field">
        <label for="lastName">Last Name</label>
        <InputText id="lastName" v-model.trim="createUser.lastName" required="true" />
      </FloatLabel>
      <FloatLabel variant="over" class="field">
        <label for="email">Email</label>
        <InputText id="email" v-model.trim="createUser.email" required="true" />
      </FloatLabel>
      <FloatLabel variant="over" class="field">
        <label for="userName">Username</label>
        <InputText id="userName" v-model.trim="createUser.username" required="true" />
      </FloatLabel>
      <FloatLabel variant="over" class="field">
        <label for="password">Password</label>
        <InputText id="password" v-model.trim="createUser.password" required="true" />
      </FloatLabel>
      <FloatLabel variant="over" class="field field-dropdown">
        <label>Permissions</label>
        <MultiSelect
            v-model="createUser.permissions"
            :options="availablePermissions"
            optionLabel="name"
            optionValue="value"
            placeholder="Select permissions"
            display="chip"
        />
      </FloatLabel>

      <template #footer>
        <Button label="Cancel" icon="pi pi-times" @click="hideDialog" class="p-button-text"/>
        <Button label="Save" icon="pi pi-check" @click="saveUser" autofocus />
      </template>
    </Dialog>

<!--    <InputText v-model="newPassword" />-->
<!--    <Button label="Change Password" @click="changePassword" />-->

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

  <Toast />
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {FilterMatchMode} from "@primevue/core";

definePageMeta({
  layout: 'default'
})

const newPassword = ref("");
const changePassword = async () => {
  const res = await useApi("/auth/change/password", {
    method: "POST",
    body: {
      "username": "mr83",
      "password": newPassword.value
    }
  })

  alert(res.data)
}

const rowClass = (row) : string => {
  if (!row.enabled) {
    return "disabled-user"
  }
}

interface User {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  userName: string;
  permissions: string[];
}

const deleteDialog = ref(false);
const userToDelete = ref<User | null>(null);
const selectedUser = ref<User>({} as User);
const submitted = ref(false);
const filters = ref({
  global: { value: null, matchMode: FilterMatchMode.CONTAINS }
});

const toast = useToast();

// Sample permissions - replace with your actual permissions
const availablePermissions = ref([
  {name: 'Admin', value: 'ROLE_ADMIN'},
  // {name: 'Editor', value: 'editor'},
  // {name: 'Viewer', value: 'viewer'},
  // {name: 'Reports', value: 'reports'},
]);

const {data: users, refresh} = await useApi<UserDTO[]>("/users/all");

// -- Create Users ------------------------------------------------------------
const createUserDialogVisible = ref<boolean>(false);
const user = ref<User>({} as User);
const createUser = ref<CreateUserDTO>({} as CreateUserDTO);
const showAddDialog = () => {
  user.value = {} as User;
  submitted.value = false;
  createUserDialogVisible.value = true;
};

const hideDialog = () => {
  createUserDialogVisible.value = false;
  submitted.value = false;
};

const editUser = (userData: User) => {
  user.value = { ...userData };
  createUser.value = userData;
  createUserDialogVisible.value = true;
};

const saveUser = async () => {
  submitted.value = true;

  const userDto = createUser.value;

  // Basic validation
  if (userDto.firstName?.trim() &&
      userDto.lastName?.trim() &&
      userDto.email?.trim() &&
      userDto.username?.trim()) {

    try {
      const res = await useClientApi("/users/create", {
        method: "POST",
        body: userDto
      });

      toast.add({ severity: 'success', summary: 'Info', detail: 'Added New User', life: 8000 })
      hideDialog();

    } catch (error) {
      toast.add({ severity: 'error', summary: 'Error Adding New User', detail: error, life: 8000 })
    }

    refresh();

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
.field {
  display: flex;
  flex-direction: column;
  margin-bottom: 1.0em;
  margin-top: 1.5em;
}

.user-management-container {
  padding: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 1em;
  width: 100%;
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

:deep(.disabled-user) {
  color: #999;
}
</style>