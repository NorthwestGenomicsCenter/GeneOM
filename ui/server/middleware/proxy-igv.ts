import { sendProxy } from 'h3'

export default defineEventHandler(async (event) => {
	if (event.path.startsWith('/igv_data/')) {
		const config = useRuntimeConfig();
		const baseUrl = config.public.igvProxyUrl;
		
		const newPath = event.path.replace(/^\/igv_data\//, '');
		const target = baseUrl + newPath;
		
		await sendProxy(event, target, {
			fetchOptions: {
				method: event.method,
				headers: event.headers
			}
		});
	}
});