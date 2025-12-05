export interface GenomicPosition {
    chr: string
    end: number
    start: number
}

export const useGlobalStore = defineStore('globalStore', () => {
	
	const gene = ref("");
	const currentGenomicPosition = ref({
		chrom: "",
		start: "1",
		end: "1000"
	});

	const setCurrentGenomicPosition = (chrom: any, start: any, end: any) => {
		currentGenomicPosition.value = {
			chrom: chrom,
			start: start,
			end: end,
		};
	}

    const updateGenomicPositon = ref<GenomicPosition>({} as GenomicPosition);
    const navigate = (position : GenomicPosition) => {
        updateGenomicPositon.value = position;
    }

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
	
	return {
		gene,
		globalNav,
		currentGenomicPosition,
        updateGenomicPositon,
		changeGene,
		addGene,
        navigate,
		setCurrentGenomicPosition,
	}
})