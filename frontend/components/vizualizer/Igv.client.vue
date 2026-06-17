<template>
  <div>
    <Header
        title="IGV View"
        disableToggle
        @settings="selectDialogVisible = true"
        @close="removeView(componentKey)"
    >
      <template #left>
        <Tag class="ml-2">Track Changes</Tag>
      </template>
    </Header>

    <div :id="componentKey" ref="igv-element"></div>

    <Dialog header="Select Samples" v-model:visible="selectDialogVisible" modal>
      <ViewerResourceTable
          :componentKey="componentKey"
          :filetypes="['bam', 'bedpe', 'narrowPeak', 'bw']"
          :visualizers="['igv']"
          v-model:visible="selectDialogVisible"
      />
    </Dialog>

    <Dialog header="Edit Resource" v-model:visible="editResourceDialogVisible" modal>
      <ResourcesEditResourceDialog
          :resource="editResource"
          v-model:visible="editResourceDialogVisible" />
    </Dialog>

    <template v-for="m in igvMounts" :key="m.id">
      <Teleport :to="m.el">
        <CommonTrackInfo v-bind="m.props" @edit="clickEditResource"/>
      </Teleport>
    </template>
  </div>
</template>

<script setup lang="ts">
import Header from "~/components/common/Header.vue";
import {type GenomicPosition, useGlobalStore} from '~/composables/useGlobal'
import {useSampleViewStore} from "~/composables/useSampleViewStore";
import {computed, createApp, createVNode, render, getCurrentInstance} from "vue";
import {CommonTrackInfo} from "#components";

defineOptions({name: "Igv"})

// Init the browser
let browser: any;
let igv: any;

const applyGlobals = ref<boolean>(false);
const globalStore = useGlobalStore();
const selectedGene = ref<string>();
const { setCurrentGenomicPosition, navigate } = globalStore;
let { gene, currentGenomicPosition, updateGenomicPositon } = storeToRefs(globalStore);

const props = defineProps<{
  componentKey: any
}>();

const selectDialogVisible = ref<boolean>(false);
const resourceStore = useViewResourcesStore();
const {selectedResources, registerConfigCallback, unregisterConfigCallback} = resourceStore;
const resources = selectedResources(props.componentKey);

let baseUrl = useRuntimeConfig().app.baseURL;
if (baseUrl === "/") {
  baseUrl = "";
}

const viewStore = useSampleViewStore();
const { layout, customReference } = storeToRefs(viewStore);
const { removeView } = viewStore;

type IGVTrack = Track & TrackConfig;

interface Track {
  id: string
  name: string
  url: string
  indexUrl?: string
  format: string
  type: string
  sort: TrackSort
}

interface TrackConfig {
  id: string
  arcOrientation?: "UP" | "DOWN"
  color?: string
  description?: Function
  flipAxis?: boolean
  height?: number
  order?: number
  visibilityWindow: number
}

interface TrackSort {
  chr: string
  position: number
  option: "BASE"
  direction: "ASC" | "DESC"
}

const clickEditResource = (resource: AnalysisResource) => {
  editResource.value = resource;
  editResourceDialogVisible.value = true
}
const editResource = ref();
const editResourceDialogVisible = ref<boolean>();

const igvTracks = computed(() => {
  const visibleTracks : IGVTrack[] = [] as IGVTrack[];

  const sortedResources = resources.value
      .sort((a, b) => {
        if (a.config == undefined || b.config == undefined) return undefined

        return a.config.order - b.config.order
      })

  for (const resource of sortedResources) {
    if (resource.technologiesViewers.map(e => e.toLowerCase()).includes("igv")) {
      const track: IGVTrack = {} as IGVTrack;
      track.id = resource.resourceId
      track.name = resource.resourceName;
      track.url = `${baseUrl}/api/omic/visualize/${resource.resourceId}/${resource.path}`;
      if (resource.path.endsWith("bam")) {
        track.indexUrl = `${baseUrl}/api/omic/visualize/${resource.resourceId}/${resource.path}.bai`;
      }
      track.type = getTrackType(resource.path);
      track.format = getTrackFormat(resource.path);
      track.visibilityWindow = 1000000;

      if (defaultTrackHeight(track.format)) {
        track.height = defaultTrackHeight(track.format);
        track.arcOrientation = "DOWN";
      }

      if (resource.config !== null) {
        let config : TrackConfig = resource.config;
        if (config?.height) {
          track.height = config.height;
        }

        if (config?.arcOrientation) {
          track.arcOrientation = config.arcOrientation;
        }

        if (config?.description) {
          track.description = config.description;
        }

        if (config?.color) {
          track.color = config.color;
        }

        if (config?.flipAxis) {
          track.flipAxis = config.flipAxis;
        }
      }

      if (resource.description) {
        track.description = () => {
          // const row = document.createElement('div')
          // row.className = 'igv-track-label-popover__row'
          // row.textContent = resource.description || resource.resourceName
          //
          // const editLink = document.createElement('a');
          // editLink.textContent = 'Edit'
          //
          // const lineBreak = document.createElement('br')
          // row.append(' ', lineBreak)
          // row.append(' ', editLink)
          //
          // editLink.addEventListener("click", () => {
          //   editResourceDialogVisible.value = true;
          //   editResource.value = resource;
          // })
          // // return row;
          // // return vueRenderer("feature")
          const el = document.createElement('div')
          const id = registerMount(el, {resource: resource})
          return el;
        }
      }

      visibleTracks.push(track);
    }
  }

  return visibleTracks;
})


/*
IGV MOUNT POINT FOR TELEPORT
 */

const igvMounts = reactive<
    { id: string; el: HTMLElement; props: any }[]
>([])

function registerMount(el: HTMLElement, props: any) {
  const id = crypto.randomUUID()
  igvMounts.push({ id, el, props })
  return id
}

function unregisterMount(id: string) {
  const idx = igvMounts.findIndex(m => m.id === id)
  if (idx >= 0) igvMounts.splice(idx, 1)
}




/*
END
 */

const defaultTrackHeight = (trackType : string) : number => {
  const heights = {
    "bedpe": 125,
    "narrowpeak": 50
  }

  return heights[trackType];
}

const getTrackFormat = (filename : string) => {
  return filename.split(".").pop().toLowerCase();
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

// Tracks that must not be removed
const defaultTracks = ['ideogram', 'ruler', 'sequence', 'Refseq Select', 'refseqAll'];

const refresh = async () => {
  if (browser == null || browser.tracks == null) return;

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

watch(updateGenomicPositon, () => {
  const pos = updateGenomicPositon.value

  browser.search(pos.chr + ":" + pos.start + "-" + pos.end);
  browser.zoomIn();
  browser.zoomIn();
  browser.zoomIn();
})

watch(layout, () => {
  refresh();
})

watch(igvTracks, () => {
  refresh();
}, {deep: true, immediate: true})

// The analysis that is being worked on
const route = useRoute()
const analysisId = computed(() => route.params.id);

let locusTimer: ReturnType<typeof setTimeout> | null = null
let lastFrame: { chr: string; start: number; end: number } | null = null
let updatedFromMessage = false;

const onLocusChange = (referenceFrameList: GenomicPosition[]) => {
  if (updatedFromMessage) {
    updatedFromMessage = false;
    return;
  }

  const frame = referenceFrameList[0]
  lastFrame = frame

  setCurrentGenomicPosition(frame.chr, frame.start, frame.end);

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

let locusEventSource = null;
const initLocusPolling = () => {
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
}

const initIgvBrowser = async () => {
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
  browser.on("trackclick", console.table)
  refresh();
}

const setTrackConfiguration = (resourceId) => {
  for (let track of browser.tracks) {
    if (resourceId === track.id) {
      const config = {} as TrackConfig;
      config.id = track.id;
      config.arcOrientation = track.arcOrientation;
      config.height = track.height;
      config.color = track.color;
      config.flipAxis = track.flipAxis;
      config.order = track.order;

      return config
    }
  }

  return {};
}

onMounted(async () => {

  // Init Polling
  initLocusPolling();
  await initIgvBrowser();

  registerConfigCallback(props.componentKey, (tracks) => setTrackConfiguration(tracks))
});

onBeforeUnmount(async () => {
  locusEventSource.close();
  // interval.close();
  unregisterConfigCallback(props.componentKey)
  destroy();
});
</script>

<style scoped>


</style>