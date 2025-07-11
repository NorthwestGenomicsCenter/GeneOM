export const useTissueStore = defineStore('tissueStore', {
	state: () => ({
		data: null,
		loaded: false,
		error: null
	}),
	actions: {
		async fetchData() {
			if (this.data !== null || this.loaded) {
				// already fetched or in-flight
				return
			}
			this.loaded = true
			this.error = null
			try {
				console.log("Fetching Featured")
				
				const res = await useApi("/tag/ontology/featured");
				this.data = res.data.value
			} catch (err) {
				this.error = err
			} finally {
				this.loaded = false
			}
		},
		async addTissue(tissue : TissueDTO) {
			if (this.data === null || !this.loaded) {
				await this.fetchData();
			}
			this.data = [...this.data, tissue]
		}
	}
})