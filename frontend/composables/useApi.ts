import type { UseFetchOptions } from 'nuxt/app'

export function useApi<T = any, DefaultT = null, E = unknown>(
	url: string | (() => string),
	options?: Omit<UseFetchOptions<T>, 'default'> & { default?: () => DefaultT | Ref<DefaultT> }
) {
	return useFetch('/api' + url, {
		...options,
		$fetch: useNuxtApp().$api as typeof $fetch
	} as UseFetchOptions<T>);
}