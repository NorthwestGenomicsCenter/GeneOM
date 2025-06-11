// https://nuxt.com/docs/api/configuration/nuxt-config
import Aura from '@primevue/themes/aura';
import Material from '@primevue/themes/material';
import * as path from "node:path";

export default defineNuxtConfig({
	alias: {'@shared': path.resolve(__dirname, '../shared')},
	compatibilityDate: '2024-04-03',
	css: [
		'primeicons/primeicons.css',
		'assets/css/main.css'
	],
	devtools: {enabled: true},
	modules: [
		'@pinia/nuxt',
		'@primevue/nuxt-module',
	],
	imports: {
		dirs: [ '~/types' ]
	},
	plugins: [
		{src: '~/plugins/jquery.js', mode: 'client'},
	],
	primevue: {
		options: {
			theme: {
				ripple: true,
				preset: Aura,
				options: {
					prefix: 'p',
					darkModeSelector: 'system',
					cssLayer: false
				}
			}
		}
	},
	runtimeConfig: {
		apiUrl: import.meta.env.API_URL,
		public: {
			igvProxyUrl: import.meta.env.IGV_PROXY_URL,
		}
	},
	ssr: true,
	nitro: {
		handlers: [
			{route: '/igv_data/**', handler: '~/server/middleware/proxy-igv'},
			{route: '/api/**', handler: '~/server/middleware/proxy-api'},
		],
		output: {
			serverDir: './.output/',
			publicDir: './.output/public'
		},
	},
	typescript: {
		strict: false
	}
})
