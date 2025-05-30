<template>
  <div class="user-state">
    <template v-if="!user.isAuthenticated" @click="handleLogin">
      <NuxtLink href="/login">Log In</NuxtLink>
    </template>

    <div v-else class="user-info">
      <span class="username">{{ user.firstName }} {{ user.lastName }}</span>
      <div class="avatar-container">
        <img :src="'/avatar.png'" :alt="username + ' avatar'" class="avatar" />
      </div>
      <div @click="logout">Log Out</div>
    </div>
  </div>
</template>

<script setup type="ts">
import { computed } from 'vue';
import { useAuth } from "~/composables/useAuth.js";
import {useRouter} from "#app";

defineOptions({
  name: 'StatusBarLogin',
})

const auth = useAuth();
const user = auth.user;

const router = useRouter()

const logout = () => {
  auth.logout();
  router.push('/login');
}

const props = defineProps({
  username: {
    type: String,
    default: '',
  },
  avatarUrl: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['login']);

const isLoggedIn = computed(() => {
  return user.isAuthenticated;
});

const handleLogin = () => {
  emit('login');
};
</script>

<style scoped>
.user-state {
  display: flex;
  align-items: center;
  font-family: Arial, sans-serif;
}

.login-button {
  padding: 8px 16px;
  background-color: #4caf9d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.login-button:hover {
  background-color: #0fa98d;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.username {
  font-weight: bolder;
  font-size: medium;
  color: #ffffff;
}

.avatar-container {
  width: 56px;
  height: 56px;
}

.avatar {
  background-color: #ffffff;
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 80px;
}
</style>