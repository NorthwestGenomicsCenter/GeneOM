export const useGlobalStore = defineStore('globalStore', () => {
	
	const gene = ref("");
	const globalNav = ref({
		gene: "",
		genes: [] as string[],
		position: "",
		isoform: ""
	})
	
	function changeGene(gene1: string) {
		globalNav.value.gene = gene1
		gene.value = gene1;
	}
	
	function addGene(gene1: string) {
		if (gene1 != null && !globalNav.value.genes.includes(gene1)) {
			globalNav.value.genes.push(gene1)
			globalNav.value.genes = [...globalNav.value.genes]
		}
	}
	
	return { gene, globalNav, changeGene, addGene }
})