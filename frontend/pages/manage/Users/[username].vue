<template>
  <section v-if="!isPageUserAccount">
    Unauthorized Access to User Account: {{ route.params.username }}
  </section>
  <section v-else>
    <h3 class="pb-10 font-bold text-4xl">
      User Setting for: {{ user.firstName }} {{ user.lastName }}
    </h3>

    <form @submit.prevent="changePassword">
      <p>
        Change Password:
      </p>
      <p>
        <Password
          v-model="oldPassword"
          placeholder="Old Password"
          :feedback="false"
          class="pb-2"
        />
      </p>
      <p>
        <Password
          v-model="newPassword"
          placeholder="New Password"
          :feedback="false"
          class="pb-2"
        />
      </p>
      <p>
        <Password
          v-model="verifyPassword"
          placeholder="Verify Password"
          :feedback="false"
          class="pb-4"
        />
      </p>
      <p>
        <Button type="submit" label="Change" :disabled="changePasswordButtonDisabled" />
      </p>
    </form>
  </section>
</template>

<script setup lang="ts">
const auth = useAuth();
const user = ref(auth.user)

const route = useRoute();
const toast = useToast();

const isPageUserAccount = (user.value.sub == route.params.username)

// definePageMeta({
//   layout: 'login-layout'
// })

const oldPassword = ref<string>("");
const newPassword = ref<string>("");
const verifyPassword = ref<string>("");

const changePassword = async () => {
  if (changePasswordButtonDisabled.value) {
    toast.add({severity: 'error', summary: `Error`, detail: `Passwords do not match`, life: 10_000})
  }

  let body : ChangePasswordRequestDTO = {
    username: user.value.sub,
    oldPassword: oldPassword.value,
    newPassword: newPassword.value
  }

  try {
    const res = await useClientApi("/users/change-password", {
      method: "POST",
      body: body
    })

    // Reset the form
    oldPassword.value = "";
    newPassword.value = "";
    verifyPassword.value = "";

    toast.add({severity: 'success', summary: `Password Changed`, detail: res, life: 5000})
  } catch (error) {
    toast.add({severity: 'error', summary: `Error`, detail: `Unable to Change Password at this time`, life: 10_000})
  }



}

const changePasswordButtonDisabled = computed(() => {
  // Must type in each box, the second rule takes care of the verification box
  if (oldPassword.value.length == 0 || newPassword.value.length == 0) {
    return true;
  }

  return newPassword.value != verifyPassword.value
})

</script>

<style scoped>

</style>