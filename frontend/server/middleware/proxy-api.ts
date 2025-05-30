// server/middleware/proxy-api.ts
import {createProxyEventHandler} from 'h3-proxy'

export default defineEventHandler(async (event) => {
	const config = useRuntimeConfig()
	
	if (!event.path.startsWith('/api/')) {
		// Not our route — continue Nuxt’s normal handling
		return
	}

	return createProxyEventHandler({
		configureProxyRequest(event) {
			return {
				headers: {
					...event.headers,
					"X-Hello": "Hello World"
				}
			}
		},
		target: config.apiUrl,
		changeOrigin: true
	})(event);
})