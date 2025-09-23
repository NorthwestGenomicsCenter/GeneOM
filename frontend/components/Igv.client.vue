<template>
  <Header title="IGV View" disableToggle>
    <template #left>
      <em class="pi pi-cog" @click="selectDialogVisible = true"/>
    </template>
    <template #right>
      <em class="pi pi-times-circle" @click="removeView(componentKey)"/>
    </template>
  </Header>
  <div :id="componentKey" ref="igv-element"></div>

  <Dialog header="Select Samples" v-model:visible="selectDialogVisible" modal>
    <ViewerResourceTable
        :componentKey="componentKey"
        :tableData="analysis.resources"
        :filetypes="['bam', 'hic', 'bw', 'bedpe']"
        @select-resources="selection"/>
  </Dialog>

</template>

<script setup lang="ts">
import Header from "~/components/common/Header.vue";
import {useGlobalStore} from '@/composables/useGlobal'
import {useSampleViewStore} from "~/composables/useSampleViewStore";
import {computed} from "vue";

defineOptions({name: "Igv"})

const applyGlobals = ref<boolean>(false);
const store = useGlobalStore();
const selectedGene = ref<string>();
let { gene, currentGenomicPosition } = storeToRefs(store)

const props = defineProps<{
  componentKey: any
}>();

const selectDialogVisible = ref<boolean>(false);
const resourceStore = useViewResourcesStore();
const {
  getViewResources,
  setViewResources,
  clearViewResources
} = resourceStore;

const resources = getViewResources(props.componentKey);

const selection = (event) => {
  setViewResources(props.componentKey, event)
}


let baseUrl = useRuntimeConfig().app.baseURL;
if (baseUrl === "/") {
  baseUrl = "";
}

const viewStore = useSampleViewStore();
// const { igvTracks, layout, customReference, analysis } = storeToRefs(viewStore);
const { layout, customReference, analysis } = storeToRefs(viewStore);
const { removeView } = viewStore;

// const igvTracks = ref([ { "name": "chrXmut.P6.cis.ROI.SV.9.7.bedpe", "url": "/api/visualize/igv/ccc6e89b-52e3-4654-8b69-db55de0bfdf5/chrXmut.P6.cis.ROI.SV.9.7.bedpe", "type": "interact", "format": "bedpe" }, { "name": "chrXmut.WT.cis.ROI.bedpe", "url": "/api/omic/igv/1ce71595-2875-4dca-bab2-410d58d88a43/chrXmut.WT.cis.ROI.bedpe", "type": "interact", "format": "bedpe" }, { "name": "P6_r2.summits.bed", "url": "/api/omic/igv/4b2d7f67-4c77-423c-b254-c0ab01d33ae9/P6_r2.summits.bed", "type": "annotation", "format": "bed" }, { "name": "P6_r2.bw", "url": "/api/omic/igv/eafd8e0f-5c6e-4716-9877-93dd3464bbb6/P6_r2.bw", "type": "wig", "format": "bw", "sync": true } ]);

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

const igvTracks = computed(() => {
  const visibleTracks : IGVTrack[] = [] as IGVTrack[];
  for (const resource of resources.value) {
    if (resource.technologiesViewers.map(e => e.toLowerCase()).includes("igv")) {
      const track: IGVTrack = {} as IGVTrack;
      track.name = resource.resourceName;
      track.url = `${baseUrl}/api/omic/visualize/${resource.resourceId}/${resource.resourceName}`;
      if (resource.resourceName.endsWith("bam")) {
        track.indexUrl = `${baseUrl}/api/omic/visualize/${resource.resourceId}/${resource.resourceName}.bai`;
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
  "bedpe": "interact",
  "narrowPeak": "annotation"
}


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
const defaultTracks = ['ideogram', 'ruler', 'sequence', 'Refseq Select', 'refseqAll'];

const refresh = async () => {
  if (browser == null || browser.tracks == null) return;
  console.table(browser.tracks);

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

const destroy = async () => {
  if (browser) {
    igv.default.removeBrowser(browser);
  }
}
onBeforeUnmount(async () => destroy());

let browser: any;
let igv: any;
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

  igv = await import('igv');
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
      genome: "hg38",
      locus: null,
      showCenterGuide: true,
    };
  }

  browser = await igv.default.createBrowser(igvDiv, options);
  window['igv'] = browser;
  browser.on("locuschange", onLocusChange);
  refresh();
});
</script>

<style scoped>


</style>