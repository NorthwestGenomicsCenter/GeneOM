import 'vuefinder/dist/style.css';
import VueFinder from 'vuefinder/dist/vuefinder';

// Registers the Vue Finder plugin for global use in the app
export default defineNuxtPlugin((nuxtApp) => {
	nuxtApp.vueApp.use(VueFinder);
})