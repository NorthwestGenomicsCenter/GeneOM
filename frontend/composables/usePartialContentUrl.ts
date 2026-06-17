export const usePartialContentUrl = () => {
	
	let baseUrl = useRuntimeConfig().app.baseURL;
	if (baseUrl === "/") {
		baseUrl = "";
	}

	const url = useRequestURL();
	
	function buildUrl(resource: AnalysisResource) : string {
		return `${url.origin}${baseUrl}/api/omic/visualize/${resource.resourceId}/${resource.resourceName}`
	}
	
	return buildUrl;
}