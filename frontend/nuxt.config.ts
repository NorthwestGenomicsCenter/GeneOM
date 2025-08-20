// https://nuxt.com/docs/api/configuration/nuxt-config
import Aura from '@primevue/themes/aura';
import Material from '@primevue/themes/material';
import * as path from "node:path";
import tailwindcss from "@tailwindcss/vite"

export default defineNuxtConfig({
	alias: {'@shared': path.resolve(__dirname, '../shared')},
	app: {
		baseURL: process.env.BASE_URL
	},
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
		dirs: [
			"types/**/*.d.ts",
			"../shared/generated/*.d.ts",
		]
	},
	plugins: [
		{src: '~/plugins/jquery.js', mode: 'client'},
	],
	vite: {
		plugins: [
			tailwindcss(),
		],
	},
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
		apiUrl: process.env.API_URL,
		public: {
			igvProxyUrl: import.meta.env.IGV_PROXY_URL,
		}
	},
	routeRules: {
		'/analysis/**': { ssr: false }, // Replace '/your-page-path' with the actual path of your page
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
