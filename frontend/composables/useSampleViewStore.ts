import {computed, onMounted, ref} from "vue";
import DynamicViewRenderer from "~/components/DynamicViewRenderer.vue";

export const useSampleViewStore = defineStore('sampleViewStore', () => {
	
	const sampleId = ref<string>(null);
	const techViewOptions = ref<SampleViewDTO[]>([]) // populated from backend
	const selectedViews = ref([])   // array of { id, viewName, technology, x, y, w, h }
	
	const queryTechViewOptions =async () => {
		techViewOptions.value = await $fetch(`/api/sample/${sampleId.value}/views`)
	}

	// Compute layout from selectedViews
	const layout = computed(() =>
		selectedViews.value.map(view => ({
			i: view.id,
			x: view.x,
			y: view.y,
			w: view.w,
			h: view.h
		}))
	)
	
	const components = computed(() =>
		selectedViews.value.map(view => ({
			id: view.id,
			component: DynamicViewRenderer,
			props: {
				viewName: view.viewName,
				sampleId: sampleId,
				technology: view.technology,
				omicResourceId: view.omicResourceId,
				fileName: view.fileName,
			}
		}))
	)
	
	
	function addView(view : SampleViewDTO) {
		selectedViews.value.push({
			id: crypto.randomUUID(), // unique per view instance
			viewName: view.viewName,
			technology: view.technology,
			omicResourceId: view.omicResourceId,
			fileName: view.fileName,
			x: 0,
			y: 0,
			w: 10,
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
		format: "bam" | "cram" | "vcf"
		type: "alignment"
		sort: TrackSort
	}
	
	interface TrackSort {
		chr: string
		position: number
		option: "BASE"
		direction: "ASC" | "DESC"
	}
	
	const igvTrack = ref<IGVTrack[]>([]);
	
	return {
		sampleId,
		techViewOptions,
		selectedViews,
		layout,
		components,
		addView,
		removeView,
		updateLayout,
		setSampleId
		
	}
})