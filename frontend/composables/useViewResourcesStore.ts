import {ref} from "vue";

export const useViewResourcesStore = defineStore('viewResourcesStore', () => {
	const resourcesMap = {} as Record<string, Ref<AnalysisResource[]>>
	const version = ref(0)
	
	function getResourceState() {
		const state = {}
		
		for (let [key, resources] of Object.entries(resourcesMap)) {
			state[key] = resources.value
		}
		
		return state
	}
	
	function updateResourceState(dbState : string) {
		const state = JSON.parse(dbState) as Record<string, AnalysisResource[]>;
		for (let [viewId, items] of Object.entries(state)) {
			setViewResources(viewId, items);
		}
	}
	
	function ensureResources(viewId: string): Ref<AnalysisResource[]> {
		let list = resourcesMap[viewId]
		if (!list) {
			list = ref<AnalysisResource[]>([])
			resourcesMap[viewId] = list
		}
		return list
	}
	
	function addViewResource(viewId: string, resource: AnalysisResource): void {
		ensureResources(viewId).value.push(resource)
		version.value++;
	}
	
	function setViewResources(viewId: string, items: AnalysisResource[]): void {
		// replace array to trigger reactivity
		clearViewResources(viewId);
		const viewRef = ensureResources(viewId);
		viewRef.value.push(...items);
		version.value++;
	}
	
	function getViewResources(viewId: string): Ref<AnalysisResource[]> {
		return ensureResources(viewId)
	}
	
	function clearViewResources(viewId: string): void {
		ensureResources(viewId).value = []
		version.value++;
	}
	
	return {
		addViewResource,
		getViewResources,
		setViewResources,
		clearViewResources,
		getResourceState,
		updateResourceState,
		version
	}
})