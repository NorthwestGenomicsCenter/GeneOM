import {ref} from "vue";

interface Configuration {
    config?: object;
}

type ConfigurableResource = AnalysisResource & Configuration

export const useViewResourcesStore = defineStore('viewResourcesStore', () => {
    const version = ref(0)

    // Updated to only hold a map of viewId: [resource1_uuid, resource2_uuid, ...]
    const _selectedResources = reactive<Record<string, ComponentResources[]>>({});
    const selectedResources = (viewId : string) => {
        return computed(() => {
            const sourceList = _resourceList.value;
            const resources = [];
            if (!_selectedResources[viewId]) return [];

            for (const key of _selectedResources[viewId]) {
                const containsResource = sourceList.some(r => r.resourceId == key.id)
                if (containsResource) {
                    let resource : ConfigurableResource = sourceList.find(r => r.resourceId == key.id)
                    resource.config = key.config;
                    resources.push(resource)
                }
            }
            return resources;
        })
    }

    const state = computed(() => {return _selectedResources})
	
	function updateResourceState(state : Record<string, ComponentResources[]>) {
        for (let [viewId, items] of Object.entries(state)) {
            _selectedResources[viewId] = items;
        }
        version.value++;
	}
	
	function setViewResources(viewId: string, items: AnalysisResource[]): void {
        try {
            if (!_selectedResources[viewId]) {
                _selectedResources[viewId] = [];
            }

            _selectedResources[viewId] = [];

            for (const item of items) {
                if (!_selectedResources[viewId].find(selectedResource => selectedResource.id === item.resourceId)) {
                    let componentResource = {} as ComponentResources;
                    componentResource.id = item.resourceId
                    _selectedResources[viewId].push(componentResource);
                }
            }
        } catch (_) {}

		version.value++;
	}

    // Set the list of resources for sub components.
    const _resourceList = ref<AnalysisResource[]>();
    const resourceList = computed(() => _resourceList);
    const setResourceList = (resourceList) => _resourceList.value = resourceList;
    const clearResourceList = () => _resourceList.value = [];

    // We need a function to callback into components to get configuration beyond the selection
    interface ConfigurationMap {
        [key: string]: Function
    }
    const configurationCallbacks: ConfigurationMap = {};
    const registerConfigCallback = (key : string, cb : Function) => {
        configurationCallbacks[key] = cb;
    }

    const unregisterConfigCallback = (key : string) => {
        delete configurationCallbacks[key]
    }

    const getComponentConfigurations = () => {
        for (let [key, cb] of Object.entries(configurationCallbacks)) {
            const resources = selectedResources(key)
            cb(resources);
        }
    }

    const getSaveResources = (): { [index: string]: ComponentResources[] } => {
        const resources = {} as { [index: string]: ComponentResources[] }

        for (let [componentKey, componentResources] of Object.entries(_selectedResources)) {
            for (let resource of componentResources) {
                if (configurationCallbacks[componentKey] !== undefined) {
                    let cb = configurationCallbacks[componentKey];
                    resource.config = cb(resource.id);
                }
            }

            resources[componentKey] = componentResources;

        }

        return resources;
    }
	
	return {
		setViewResources,
		updateResourceState,
        setResourceList,
        clearResourceList,
        selectedResources,
        resourceList,
        state,
        registerConfigCallback,
        unregisterConfigCallback,
        getSaveResources
	}
})