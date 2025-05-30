export default defineNuxtPlugin(async (nuxtApp) => {
	const { getToken } = useAuth()
	
	await getToken()
	
	return {
		provide: {
			auth: {
				check: getToken
			}
		}
	}
})