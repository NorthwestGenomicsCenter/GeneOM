<template>
  <Header title="IGV View" disableToggle />
  <Select
      placeholder="Navigate to Gene"
      v-model="selectedGene"
      :options="store.globalNav.genes" />
  <div id="igv-div" ref="igv-element"></div>
</template>

<script setup lang="ts">
import igv from 'igv';
import Header from "~/components/common/Header.vue";
import {useGlobalStore} from '@/composables/useGlobal'

defineOptions({name: "Igv"})

let browser: any;
onMounted(async () => {
  await loadIgv();
})

const applyGlobals = ref<boolean>(false);
const store = useGlobalStore();
const selectedGene = ref<string>();
let {gene} = storeToRefs(store)

watch(selectedGene, () => {
  browser.search(gene.value);
  browser.zoomIn();
  browser.zoomIn();
  browser.zoomIn();
})

const props = defineProps({
  grc38: {
    type: Boolean,
    default: false
  },
  chromosome: {
    type: String,
    default: undefined
  },
  position: {
    type: String,
    default: undefined
  },
  windowSize: {
    type: String,
    default: 50
  }
})

let locus = () => {
  if (props.chromosome === undefined || props.position === undefined) {
    return null;
  } else {
    let start = parseInt(props.position) - parseInt(props.windowSize)
    let end = parseInt(props.position) + parseInt(props.windowSize)
    return `chr${props.chromosome}:${start}-${end}`;
  }
}

const exampleTracks = [
  {
    "name": "NA12878",
    "url": "/igv_data/samplify/ServeIgvDataFile/1917.bam",
    "indexURL": "/igv_data/samplify/ServeIgvDataFile/1917.bam.bai",
    "format": "bam",
    "type": 'alignment',
    "sort": {
      chr: "1",
      position: 1,
      option: "BASE",
      direction: "ASC"
    },
  }
]

const tracks = [
  {
    "url": "./igv_data/3068.bam",
    "indexURL": "./igv_data/3068.bam.bai",
    "name": "LCL_100_150K.singlelinker.paired.nr.sorted.bam",
    "format": "bam",
    "type": 'alignment'
  },
  // {
  //   "url": "./igv_data/3089.bam",
  //   "name": "1662030.whole_genome.bam",
  //   "indexURL": "./igv_data/3088.bam.bai",
  //   "format": "bam",
  //   "type": 'alignment'
  // },
  {
    "url": "./igv_data/3072.bedpe",
    "name": "LCL_100_150K.cis.DA.bedpe",
    "format": "bedpe"
  },
  {
    "url": "./igv_data/3076.bedpe",
    "name": "SRR17666402.cis.DA.bedpe",
    "format": "bedpe"
  },
  {
    "url": "./igv_data/3075.bedpe",
    "name": "SRR17666401.cis.DA.bedpe",
    "format": "bedpe"
  },
  {
    "url": "./igv_data/3073.bedpe",
    "name": "SRR17666399.cis.DA.bedpe",
    "format": "bedpe"
  },
  {
    "url": "./igv_data/3074.bedpe",
    "name": "SRR17666400.cis.DA.bedpe",
    "format": "bedpe"
  },
  {
    "url": "./igv_data/3123.bw",
    "name": "1673059.10.forward.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3124.bw",
    "name": "1673061.10.reverse.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3125.bw",
    "name": "1673061.10.forward.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3126.bw",
    "name": "1673061.X.reverse.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3127.bw",
    "name": "1673061.X.forward.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3092.bw",
    "name": "1660126.10.forward.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3093.bw",
    "name": "1660126.10.reverse.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3094.bw",
    "name": "1660126.X.reverse.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3095.bw",
    "name": "1660126.X.forward.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3110.bw",
    "name": "LCL_100_150K.treat_pileup.NDP.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3111.bw",
    "name": "SRR17666399.treat_pileup.NDP.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3112.bw",
    "name": "SRR17666400.treat_pileup.NDP.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3113.bw",
    "name": "SRR17666401.treat_pileup.NDP.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3114.bw",
    "name": "SRR17666402.treat_pileup.NDP.bw",
    "format": "bw"
  },

]

const tracks2 = [
  {
    "url": "./igv_data/3077.gz",
    "name": "LCL_100_150K.trans.DA.bedpe.gz",
    "format": "bedpe",
    "type": "feature"
  },


  {"url": "./igv_data/3108.bam", "name": "mapped.bam", "format": "bam"},
  {
    "url": "./igv_data/3109.bai",
    "name": "mapped.bam.bai",
    "format": "bai"
  },
  {
    "url": "./igv_data/3115.bw",
    "name": "1673059.X.forward.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3116.bw",
    "name": "1673059.X.reverse.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3117.bw",
    "name": "1673059.10.reverse.bw",
    "format": "bw"
  },

  {
    "url": "./igv_data/3119.bw",
    "name": "1673060.10.reverse.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3120.bw",
    "name": "1673060.10.forward.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3121.bw",
    "name": "1673060.X.forward.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3122.bw",
    "name": "1673060.M.reverse.bw",
    "format": "bw"
  },
  {
    "url": "./igv_data/3128.narrowPeak",
    "name": "LCL_100_150K_peaks.narrowPeak",
    "format": "narrowPeak"
  },
  {
    "url": "./igv_data/3129.bed",
    "name": "differential_peaks_only_analysis.FDR_0.05.annotated.bed",
    "format": "bed"
  }]

async function loadIgv() {
  let igvDiv = document.getElementById("igv-div");
  let options =
      {
        genome: props.grc38 ? "hg38" : "hg19",
        locus: locus(),
        tracks: tracks,
        showCenterGuide: true,
      };

  browser = await igv.createBrowser(igvDiv, options);
}
</script>

<style scoped>


</style>