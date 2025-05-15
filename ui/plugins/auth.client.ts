// plugins/auth.client.ts
export default defineNuxtPlugin(async (nuxtApp) => {
	const { getToken } = useAuth()
	
	// Check auth status on app init
	await getToken()
	
	// You can add global auth helpers here
	return {
		provide: {
			auth: {
				check: getToken
			}
		}
	}
})