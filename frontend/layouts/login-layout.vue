<template>
  <div class="layout">
    <div class="container" :style="{ backgroundImage: `url('${baseUrl}dna3.png')` }">
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
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  margin: 0;
  padding: 0;
}

.container {
  background-image: url("@/public/dna3.png");
  background-position: center;
  background-repeat: no-repeat;
  background-size: 100%;
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
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