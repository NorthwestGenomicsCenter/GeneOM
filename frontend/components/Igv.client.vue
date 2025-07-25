<template>
  <Header title="IGV View" disableToggle />
  <Select
      placeholder="Navigate to Gene"
      v-model="selectedGene"
      :options="store.globalNav.genes" />
  <div id="igv-div" ref="igv-element"></div>
  <Button label="Refresh" @click="refresh" />
</template>

<script setup lang="ts">
import Header from "~/components/common/Header.vue";
import {useGlobalStore} from '@/composables/useGlobal'

defineOptions({name: "Igv"})

const applyGlobals = ref<boolean>(false);
const store = useGlobalStore();
const selectedGene = ref<string>();
let { gene, currentGenomicPosition } = storeToRefs(store)

watch(selectedGene, () => {
  browser.search(gene.value);
  browser.zoomIn();
  browser.zoomIn();
  browser.zoomIn();
})

watch(currentGenomicPosition, () => {
  const pos = currentGenomicPosition.value

  browser.search(pos.chrom + ":" + pos.start + "-" + pos.end);
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
  },
  omicResourceId: {
    type: String,
    default: null
  },
  sampleId: {
    type: String,
    default: null
  },
  technology: {
    type: String,
    default: null
  },
  viewName: {
    type: String,
    default: null
  },
  fileName: {
    type: String,
    default: null
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

const tracks = [
  {
    "name": "NA12878",
    "url": `/api/omic/igv/${props.omicResourceId}/${props.fileName}`,
    "indexURL": `/api/omic/igv/${props.omicResourceId}/${props.fileName}.bai`,
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

const refresh = () => {
  browser.visibilityChange();
}

async function loadIgv(igv) {
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

let browser: any;
onMounted(async () => {
  const igv = await import('igv');
  const igvDiv = document.getElementById("igv-div");
  if (!igvDiv) return;

  const options = {
    genome: props.grc38 ? "hg38" : "hg19",
    locus: locus(),
    tracks: tracks,
    showCenterGuide: true,
  };

  browser = await igv.default.createBrowser(igvDiv, options);
});
</script>

<style scoped>


</style>