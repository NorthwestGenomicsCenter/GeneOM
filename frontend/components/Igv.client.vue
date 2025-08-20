<template>
  <Header title="IGV View" disableToggle>
    <template #left>
      <em class="pi pi-cog" @click="a"/>
    </template>
    <template #right>
      <em class="pi pi-times-circle" @click="removeView(componentKey)"/>
    </template>
  </Header>
<!--  <Select-->
<!--      placeholder="Navigate to Gene"-->
<!--      v-model="selectedGene"-->
<!--      :options="store.globalNav.genes" />-->
  <div :id="componentKey" ref="igv-element"></div>
<!--  <Button label="Refresh" @click="refresh" />-->
</template>

<script setup lang="ts">
import Header from "~/components/common/Header.vue";
import {useGlobalStore} from '@/composables/useGlobal'
import {useSampleViewStore} from "~/composables/useSampleViewStore";

defineOptions({name: "Igv"})

const applyGlobals = ref<boolean>(false);
const store = useGlobalStore();
const selectedGene = ref<string>();
let { gene, currentGenomicPosition } = storeToRefs(store)

const props = defineProps<{
  componentKey: any
}>();

let baseUrl = useRuntimeConfig().app.baseURL;
if (baseUrl === "/") {
  baseUrl = "";
}

const viewStore = useSampleViewStore();
const { igvTracks, layout, customReference } = storeToRefs(viewStore);
const { removeView } = viewStore;

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


watch(layout, () => {
  refresh();
})

watch(igvTracks, () => {
  refresh();
})

// Tracks that must not be removed
const defaultTracks = ['ideogram', 'ruler', 'sequence', 'Refseq Select'];

const refresh = async () => {
  const currentIds = browser.tracks.map(t => t.id);
  const newIds = igvTracks.value.map(t => t.name);

  // Remove tracks not in the new list
  for (const track of browser.tracks) {
    if (!newIds.includes(track.id) && !defaultTracks.includes(track.id)) {
      await browser.removeTrack(track);
    }
  }

  // Load any new tracks not already present
  for (const newTrack of igvTracks.value) {
    if (!currentIds.includes(newTrack.name)) {
      await browser.loadTrack(newTrack);
    }
  }

  browser.visibilityChange();
}

// The analysis that is being worked on
const route = useRoute()
const analysisId = computed(() => route.params.id);

let locusTimer: ReturnType<typeof setTimeout> | null = null
let lastFrame: { chr: string; start: number; end: number } | null = null
let updatedFromMessage = false;

const onLocusChange = (referenceFrameList: any[]) => {
  if (updatedFromMessage) {
    updatedFromMessage = false;
    return;
  }

  const frame = referenceFrameList[0]
  lastFrame = frame

  if (locusTimer) clearTimeout(locusTimer)

  locusTimer = setTimeout(async () => {
    if (!lastFrame) return
    const f = lastFrame
    const body = {
      updateTime: new Date(),
      message: `${f.chr}:${f.start}-${f.end}`
    };

    await useClientApi(`/analysis/update/locus/${analysisId.value}`, {
      method: 'POST',
      body: body,
    })
    lastFrame = null
    locusTimer = null
  }, 250)
}

let browser: any;
let locusEventSource = null;
onMounted(async () => {
  locusEventSource = new EventSource(`${baseUrl}/api/analysis/locus/${analysisId.value}`);

  locusEventSource.onmessage = (event) => {
    if (event.data.includes("heartbeat")) return;
    updatedFromMessage = true;
    browser.search(event.data);
  };

  locusEventSource.onerror = (error) => {
    console.error('SSE Error:', error);
    locusEventSource.close();
  }

  const igv = await import('igv');
  const igvDiv = document.getElementById(props.componentKey);
  if (!igvDiv) return;

  let options;

  // We need to remove the genome from the options block and add the custom reference.
  if (customReference.value != null) {
    options = {
      reference: {
        ...customReference.value
      },
      locus: null,
      showCenterGuide: true,
    };
  } else {
    options = {
      genome: props.grc38 ? "hg38" : "hg19",
      locus: null,
      showCenterGuide: true,
    };
  }

  browser = await igv.default.createBrowser(igvDiv, options);
  browser.on("locuschange", onLocusChange);
  refresh();
});
</script>

<style scoped>


</style>