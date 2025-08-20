<template>
  <section class="login-page">
    <div class="login-box">
      <img
          :src="'logo.png'"
          alt="NWGC Multiomics Browser"
          class="logo"/>
      <div class="login-form">
        <form @submit.prevent="handleLogin">
          <div class="credentials">
            <label for="username">Username:</label>
            <InputText id="username" v-model="credentials.username" placeholder="Username"/>
          </div>
          <div class="credentials">
            <label for="password">Password:</label>
            <InputText id="password" v-model="credentials.password" type="password" placeholder="Password"/>
          </div>
          <div>
            <Button
              rounded
              raised
              size="large"
              type="submit">Login</Button>
          </div>
        </form>
      </div>
    </div>
  </section>
  <Toast />
</template>

<script setup type="ts">
const credentials = ref({ username: 'mr83', password: '12345' })
const { login } = useAuth()
const router = useRouter()
const toast = useToast()

definePageMeta({
  layout: 'login-layout'
})


const handleLogin = async () => {
  const {data, error, isAuthenticated} = await login(credentials.value)

  if (isAuthenticated.value) {
    await router.push('/')
  } else if (error) {
    toast.add({ severity: 'error', summary: 'Info', detail: 'Error Logging In', life: 3000 })
    console.table(error)
  }
}
</script>

<style scoped>
.credentials {
  padding-bottom: 0.5em;
}

.credentials > label {
  padding-right: 0.5em;
}

.login-box {
  background-color: #ffffff;
  border-radius: 5px;
  border: 1px solid #ccc;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  height: 35em;
  padding: 20px;
  text-align: center;
  width: 30em;
  z-index: 2;
}

.login-page {
  background: #ffffff;
  display: flex;
  flex-direction: column;
}

.logo {
  padding-bottom: 2em;
  padding-top: 4em;
  width: 25em;
}
</style>