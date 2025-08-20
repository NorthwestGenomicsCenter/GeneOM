import type { NitroFetchOptions } from 'nitropack/types'

export function useClientApi<T = any, E = unknown>(
	url: string | (() => string),
	options?: NitroFetchOptions<any>,
) {
	url = '/api' + url;
	
	return $fetch<T>(url, {
		...options
	});
}