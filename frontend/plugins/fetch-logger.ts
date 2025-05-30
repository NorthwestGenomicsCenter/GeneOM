export default defineNuxtPlugin((nuxtApp) => {
	const originalFetch = $fetch
	
	// Override $fetch globally
	nuxtApp.$fetch = async function (request, options) {
		console.log('[FETCH] Request URL:', request)
		console.log('[FETCH] Options:', options)
		
		try {
			const response = await originalFetch(request, options)
			console.log('[FETCH] Response:', response)
			return response
		} catch (error) {
			console.error('[FETCH] Error:', error)
			throw error
		}
	}
})