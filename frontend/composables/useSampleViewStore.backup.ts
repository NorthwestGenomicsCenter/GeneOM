import {computed, onMounted, ref} from "vue";
import DynamicViewRenderer from "~/components/DynamicViewRenderer.vue";
import {useRoute} from "#vue-router";

export const useSampleViewStore = defineStore('sampleViewStore', () => {
	const sampleId = ref<string>(null);
	const techViewOptions = ref<SampleViewDTO[]>([]) // populated from backend
	const selectedViews = ref([])   // array of { id, viewName, technology, x, y, w, h }
	
	const queryTechViewOptions = async () => {
		techViewOptions.value = await $fetch(`/api/sample/${sampleId.value}/views`)
	}

	// Compute layout from selectedViews
	const layout = computed(() => {
		console.log("Changed Layout");
		return selectedViews.value.map(view => ({
			i: view.id,
			x: view.x,
			y: view.y,
			w: view.w,
			h: view.h
		}))
	})
	
	const layoutMap = computed(() => {
		console.log('Changed Layout')
		return new Map(
			selectedViews.value.map(v => [v.id, { i: v.id, x: v.x, y: v.y, w: v.w, h: v.h }])
		)
	})
	
	const components = computed(() =>
		selectedViews.value.map(view => ({
			id: view.id,
			component: DynamicViewRenderer,
			props: {
				key: view.id,
				viewName: view.viewName,
				sampleId: sampleId,
				technology: view.technology,
				omicResourceId: view.omicResourceId,
				fileName: view.fileName,
			}
		}))
	)
	
	const customReference = ref<Object>();
	const setCustomReference = (customRef) => {
		customReference.value = customRef;
	}
	
	const resourcesMap = ref<Object>()
	
	function ensureResources(viewId: string): Ref<AnalysisResource[]> {
		let list = resourcesMap.value[viewId]
		if (!list) {
			list = ref<AnalysisResource[]>()
			resourcesMap.value[viewId] = list
		}
		return list
	}
	
	function addViewResource(viewId: string, resource: AnalysisResource): void {
		ensureResources(viewId).value.push(resource)
	}
	
	function setViewResources(viewId: string, items: AnalysisResource[]): void {
		// replace array to trigger reactivity
		const viewRef = ensureResources(viewId)
		viewRef.value.push(...items)
	}
	
	function getViewResources(viewId: string): Ref<AnalysisResource[]> {
		debugger;
		return ensureResources(viewId)
	}
	
	function clearViewResources(viewId: string): void {
		ensureResources(viewId).value = []
	}
	
	function addView(view : SampleViewDTO) {
		selectedViews.value.push({
			id: crypto.randomUUID(), // unique per view instance
			viewName: view.viewName,
			technology: view.technology,
			omicResourceId: view.omicResourceId,
			fileName: view.fileName,
			x: 0,
			y: 0,
			w: 40,
			h: 15
		})
	}
	
	function removeView(viewId: string) {
		selectedViews.value = selectedViews.value.filter(v => v.id !== viewId)
	}
	
	function updateLayout(newLayout) {
		newLayout.forEach(updated => {
			const match = selectedViews.value.find(v => v.id === updated.i)
			if (match) {
				match.x = updated.x
				match.y = updated.y
				match.w = updated.w
				match.h = updated.h
			}
		})
	}
	
	const setSampleId = async (id : any) => {
		sampleId.value = id;
		await queryTechViewOptions();
	}
	
	interface IGVTrack {
		name: string
		url: string
		indexUrl?: string
		format: string
		type: string
		sort: TrackSort
	}
	
	interface TrackSort {
		chr: string
		position: number
		option: "BASE"
		direction: "ASC" | "DESC"
	}
	
	const selectedResources = ref<AnalysisResource[]>([]);
	
	const addAnalysisResource = (resources: AnalysisResource[]) => {
		selectedResources.value = [];
		for (let resource of resources) {
			selectedResources.value.push(resource)
		}
	}
	
	/*
	{
	    "name": "NA12878",
	    "url": `${baseUrl}/api/visualize/igv/${props.omicResourceId}/${props.fileName}`,
	    "indexURL": `${baseUrl}/api/visualize/igv/${props.omicResourceId}/${props.fileName}.bai`,
	    "format": "bam",
	    "type": 'alignment',
	    "sort": {
	      chr: "1",
	      position: 1,
	      option: "BASE",
	      direction: "ASC"
	    },
	  }
	 */
	
	let baseUrl = useRuntimeConfig().app.baseURL;
	if (baseUrl === "/") {
		baseUrl = "";
	}
	
	const igvTracks = computed(() => {
		const visibleTracks : IGVTrack[] = [] as IGVTrack[];
		for (const resource of selectedResources.value) {
			if (resource.technologiesViewers.map(e => e.toLowerCase()).includes("igv")) {
				const track: IGVTrack = {} as IGVTrack;
				track.name = resource.resourceName;
				track.url = `${baseUrl}/api/visualize/igv/${resource.resourceId}/${resource.resourceName}`;
				if (resource.resourceName.endsWith("bam")) {
					track.indexUrl = `${baseUrl}/api/visualize/igv/${resource.resourceId}/${resource.resourceName}.bai`;
				}
				track.type = getTrackType(resource.resourceName);
				track.format = getTrackFormat(resource.resourceName);
				
				visibleTracks.push(track);
			}
		}
		
		return visibleTracks;
	})
	
	const getTrackFormat = (filename : string) => {
		return filename.split(".").pop();
	}
	
	const getTrackType = (filename : string) => {
		const suffix = filename.split(".").pop();
		return trackTypes[suffix];
	}
	
	const trackTypes = {
		"wig": "wig",
		"bigWig": "wig",
		"bedGraph": "wig",
		"bam": "alignment",
		"cram": "alignment",
		"vcf": "variant",
		"seg": "seg",
		"bed": "annotation",
		"gff": "annotation",
		"gff3": "annotation",
		"gtf": "annotation",
		"bedpe": "interact"
	}
	
	// const igvTracks = ref<IGVTrack[]>([]);
	const route = useRoute()
	const analysisId = computed(() => route.params.id);
	const analysis = ref<AnalysisViewDTO>();

    const setAnalysis = async(a : AnalysisViewDTO) => {
        analysis.value = a;
    }
	
	window['resourcesMap'] = resourcesMap;

    function reset() {
        analysis.value = {} as AnalysisViewDTO;
        resourcesMap.value = null;
        sampleId.value = null;
        selectedResources.value = null;
        selectedViews.value = [];
        techViewOptions.value = null;
    }

	return {
		components,
		customReference,
		igvTracks,
		layout,
		layoutMap,
		resourcesMap,
		sampleId,
		selectedResources,
		selectedViews,
		techViewOptions,
		addAnalysisResource,
		addView,
		addViewResource,
		clearViewResources,
		getViewResources,
		removeView,
		setCustomReference,
		setSampleId,
		setViewResources,
		updateLayout,
        reset,

        setAnalysis
	}
})