import type { UseFetchOptions } from 'nuxt/app'

export function useApi<T = any, E = unknown>(
	url: string | (() => string),
	options?: UseFetchOptions<T>,
) {
	url = '/api' + url;
	
	return useFetch(url, {
		...options,
		$fetch: useNuxtApp().$api as typeof $fetch
	});
}