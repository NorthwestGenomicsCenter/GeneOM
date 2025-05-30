<template>
  <div class="search-button-container">
    <a href="#">
      <slot/>
    </a>
  </div>
</template>

<script setup lang="ts">

</script>

<style scoped>
.search-button-container {
  text-align: center;
}
.search-button-container a {
  display: inline-block;
  color: #fff;
  text-decoration: none;
  font-weight: 600;
  padding: 0.6em 2.5em;
  border: 0.18em solid transparent;
  background: #4b2e83;
  border-radius: 3.5em;
  position: relative;
  z-index: 1;
  overflow: hidden;
}
.search-button-container a:before {
  content: "";
  display: block;
  border-radius: 3.5em;
  position: absolute;
  background: inherit;
  top: 0.2em;
  left: 0.2em;
  right: 0.2em;
  bottom: 0.2em;
  z-index: -1;
}
.search-button-container a:after {
  content: "";
  display: block;
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  width: 100%;
  z-index: -2;
  transform: rotate(0deg);
  animation: gradient 2s ease alternate infinite;
  -webkit-animation-play-state: paused;
  animation-play-state: paused;
}
.search-button-container a:hover:after {
  -webkit-animation-play-state: running;
  animation-play-state: running;
}

.search-button-container a:nth-child(1):after {
  background: #e636c4;
  background: -moz-linear-gradient(left, #e636c4 0%, #3fcea5 48%, #50408b 96%);
  background: -webkit-linear-gradient(left, #e636c4 0%, #3fcea5 48%, #50408b 96%);
  background: linear-gradient(to right, #e636c4 0%, #3fcea5 48%, #50408b 96%);
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#e636c4', endColorstr='#50408b', GradientType=1);
  background-size: 300% 300%;
}
@-webkit-keyframes gradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 95% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}
@keyframes gradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 95% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}
</style>