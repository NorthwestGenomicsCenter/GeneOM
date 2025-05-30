import { ofetch } from 'ofetch'

/**
 * Nuxt plugin that wraps the default `$fetch` to automatically extract
 * authentication tokens from response headers and update cookies.
 *
 * @plugin
 * @returns {void}
 */
export default defineNuxtPlugin(() => {
	/**
	 * Hook that runs after every successful HTTP response.
	 * Scans for `x-access-token` and `x-refresh-token` headers,
	 * then updates the auth cookies via the `useAuth` composable.
	 *
	 * @async
	 * @method onResponse
	 * @param {object} context             - Hook context
	 * @param {RequestInfo} context.request  - Original request info
	 * @param {Response}    context.response - Fetch API Response object
	 * @returns {Promise<void>}
	 */
	(globalThis as any)['$fetch'] = ofetch.create({
		async onResponse({ request, response }) {
			let accessToken = null;
			let refreshToken = null;
			
			for (const [name, value] of response.headers.entries()) {
				if (name === "x-access-token") {
					accessToken = value;
				} else if (name === "x-refresh-token") {
					refreshToken = value;
				}
			}
			
			if (accessToken && refreshToken) {
				const auth = useAuth();
				auth.setAuthCookies(accessToken, refreshToken)
			}
		}
	})
})