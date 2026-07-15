import {computed, onMounted, ref} from "vue";
import DynamicViewRenderer from "~/components/grid-layout/DynamicViewRenderer.vue";
import {useRoute} from "#vue-router";

export type LayoutPosition = {
	i: string,
	x: string,
	y: string,
	w: string,
	h: string,
	viewName: string
}

export const useSampleViewStore = defineStore('sampleViewStore', () => {

    // This should be the only ref object in this store
    let _analysis = ref<AnalysisViewDTO>();
    const analysis = computed(() => _analysis);
    const setAnalysis = async(a) => {
        _analysis.value = a;
    }

    const sampleId = ref<string>(null);
	const techViewOptions = ref<SampleViewDTO[]>([]) // populated from backend
	const _selectedViews = ref([])   // array of { id, viewName, technology, x, y, w, h }
    const setSelectedView = (view) => {
        _selectedViews.value = view;
    }
    const selectedViews = computed(() => _selectedViews.value);

    function reset() {
        _analysis.value = {} as AnalysisViewDTO;
        _selectedViews.value = [];
        sampleId.value = null;
        techViewOptions.value = null;
    }


	const queryTechViewOptions = async () => {
		techViewOptions.value = await $fetch(`/api/sample/${sampleId.value}/views`)
	}

	// Compute layout from selectedViews
	const layout = computed<LayoutPosition[]>(() => {
		return _selectedViews.value.map(view => ({
			i: view.id,
			x: view.x,
			y: view.y,
			w: view.w,
			h: view.h,
			viewName: view.viewName
		}))
	})
	
	const layoutMap = computed(() => {
		console.log('Changed Layout')
		return new Map(
			_selectedViews.value.map(v => [v.id, { i: v.id, x: v.x, y: v.y, w: v.w, h: v.h }])
		)
	})
	
	const components = computed(() =>
		_selectedViews.value.map(view => ({
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
	
	function addView(view : SampleViewDTO) {
		_selectedViews.value.push({
			id: crypto.randomUUID(), // unique per view instance
			viewName: view.viewName,
			technology: view.technology,
			omicResourceId: view.omicResourceId,
			fileName: view.fileName,
			x: 0,
			y: 0,
			w: 40,
			h: 15,
		})
	}
	
	function removeView(viewId: string) {
		_selectedViews.value = _selectedViews.value.filter(v => v.id !== viewId)
	}
	
	function updateLayout(newLayout) {
		newLayout.forEach(updated => {
			const match = _selectedViews.value.find(v => v.id === updated.i)
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

	let baseUrl = useRuntimeConfig().app.baseURL;
	if (baseUrl === "/") {
		baseUrl = "";
	}

	return {
        setAnalysis,
        reset,
        analysis,
		components,
		customReference,
		layout,
		layoutMap,
		sampleId,
		selectedViews,
		techViewOptions,
		addView,
		removeView,
		setCustomReference,
		setSampleId,
		updateLayout,
        setSelectedView

	}
})