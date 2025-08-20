<template>
  <div class="layout">
    <div class="background-blur" :style="{ backgroundImage: `url('${baseUrl}dna3.png')` }"></div>
    <div class="login-container">
      <slot /> <!-- This is where your page content will be injected -->
    </div>
    <footer>
      <slot name="footer"> <!-- Optional named slot for footer content -->
        &copy; 2025 Northwest Genomics Center, University of Washington. All rights reserved.
      </slot>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {useGlobalStore} from "~/composables/useGlobal.ts";
import Header from "~/components/common/Header.vue";
const baseUrl = useRuntimeConfig().app.baseURL;

useHead({
  style: [
    {
      children: `:root { --app-base-url: "${baseUrl}"; }`,
    },
  ],
  link: [
    {
      rel: 'preconnect',
      href: 'https://fonts.googleapis.com',
    },
    {
      rel: 'preconnect',
      href: 'https://fonts.gstatic.com',
      crossorigin: 'anonymous',
    },
    {
      rel: 'stylesheet',
      href: 'https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&display=swap',
    },
  ],
});

</script>

<style scoped>
:root {
  /* fallback, gets overridden by runtime */
  --app-base-url: "/";
}

.layout {
  display: flex;
  flex-direction: column;
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100vh;
}

.login-container {
  /* Remove fixed width/height and allow flex growth */
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;

  /* Ensure it stretches to the full available space */
  width: 100% !important;
  height: 100%;

  /* Background image styling to stretch it */
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover; /* ⬅ ensures the image covers the whole area */
}

.background-blur {
  /* Remove fixed width/height and allow flex growth */
  position: absolute;
  top: 0;
  left: 0;
  display: block;
  justify-content: center;
  align-items: center;
  padding: 20px;

  /* Ensure it stretches to the full available space */
  width: 100%;
  height: 100%;

  /* Background image styling to stretch it */
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover; /* ⬅ ensures the image covers the whole area */
  filter: blur(2px);
}

footer {
  background-color: #b7a57a;
  color: white;
  text-align: center;
  height: 3em;
  padding: 0.5em;
  font-size: 1em;
  border-top: 1px solid #ddd;
}
</style>