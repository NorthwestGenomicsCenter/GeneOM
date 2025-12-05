import {ref} from "vue";

export const useViewResourcesStore = defineStore('viewResourcesStore', () => {
	// const resourcesMap = {} as Record<string, Ref<AnalysisResource[]>>
    const resourcesMap = ref(new Map<string, Ref<AnalysisResource[]>>())
    const version = ref(0)

    // Updated to only hold a map of viewId: [resource1_uuid, resource2_uuid, ...]
    const _selectedResources = reactive<Record<string, string[]>>({});

    const selectedResources = (viewId : string) => {
        return computed(() => {
            const sourceList = _resourceList.value;
            const resources = [];
            for (const key of _selectedResources[viewId]) {
                const containsResource = sourceList.some(r => r.resourceId == key)
                if (containsResource) {
                    let resource = sourceList.find(r => r.resourceId == key)
                    resources.push(resource)
                }
            }
            return resources;
        })
    }

    const state = computed(() => {return _selectedResources})
	
	const getResourceState = () => {
		const state = {}

		for (let [key, resources] of Object.entries(resourcesMap.value)) {
			state[key] = resources.value
		}

        return JSON.stringify(state)
	}
	
	function updateResourceState(dbState : string) {
        const state = JSON.parse(dbState) as Record<string, string[]>;
        for (let [viewId, items] of Object.entries(state)) {
            _selectedResources[viewId] = items;
        }
        version.value++;
	}
	
	function ensureResources(viewId: string): Ref<AnalysisResource[]> {
		let list = resourcesMap.value[viewId]
		if (!list) {
			list = ref<AnalysisResource[]>([])
			resourcesMap.value[viewId] = list
		}
		return list
	}
	
	function addViewResource(viewId: string, resource: AnalysisResource): void {
		ensureResources(viewId).value.push(resource)

		version.value++;
	}
	
	function setViewResources(viewId: string, items: AnalysisResource[]): void {
        try {
            if (!_selectedResources[viewId]) {
                _selectedResources[viewId] = [];
            }

            _selectedResources[viewId] = [];

            for (const el of items) {
                if (!_selectedResources[viewId].includes(el.resourceId)) {
                    _selectedResources[viewId].push(el.resourceId);
                }
            }
        } catch (_) {}

        // replace array to trigger reactivity
		clearViewResources(viewId);
		const viewRef = ensureResources(viewId);
		viewRef.value.push(...items);
		version.value++;
	}
	
	function getViewResources(viewId: string): Ref<AnalysisResource[]> {
		return ensureResources(viewId)
	}

    function getComputedViewResources(viewId: string): ComputedRef<Ref<AnalysisResource[], AnalysisResource[]>> {
        return computed(() => ensureResources(viewId));
    }
	
	function clearViewResources(viewId: string): void {
		ensureResources(viewId).value = []
		version.value++;
	}

    // Set the list of resources for sub components.
    const _resourceList = ref<AnalysisResource[]>();
    const resourceList = computed(() => _resourceList);
    const setResourceList = (resourceList) => _resourceList.value = resourceList;
    const clearResourceList = () => _resourceList.value = [];
	
	return {
		addViewResource,
		getViewResources,
		setViewResources,
		clearViewResources,
		getResourceState,
		updateResourceState,
        setResourceList,
        clearResourceList,
        resourceList,
		version,
        selectedResources,
        state
	}
})