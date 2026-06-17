<template>
  <div>
    <section ref="panel" class="panel">
      <CommonHeader
          title="Juice Box"
          @close="removeView(componentKey)"
          @settings="onSettings">
      </CommonHeader>
      <div
          :id="componentKey"
          class="juice-box-app"
          style="width: 640px; height: 480px"
          ref="juiceboxRef">
      <div class="content-container">
        </div>
      </div>

    </section>

    <Dialog header="Select Samples" v-model:visible="selectDialogVisible" modal>
      <ViewerResourceTable
          :componentKey="componentKey"
          :visualizers="['hic']"
          v-model:visible="selectDialogVisible" />
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import {useSampleViewStore} from "~/composables/useSampleViewStore";
import {useGlobalStore} from "~/composables/useGlobal";

const juiceboxRef = ref<HTMLElement | null>(null)
let browser = null;

const props = defineProps<{
  componentKey: any
}>();

interface JuiceBoxConfig {
  backgroundColor?: string;
  url: string;
  name?: string;
  state?: string;
  colorScale?: string;
  nvi?: string;
  controlUrl?: string;
  controlName?: string;
  displayMode?: "AOB" | string;
  controlNvi?: string;
  height?: string;
  width?: string;
}

const globalStore = useGlobalStore();
const { setCurrentGenomicPosition } = globalStore;
const { currentGenomicPosition } = storeToRefs(globalStore);

const selectDialogVisible = ref<boolean>(false);
const onSettings = async () => {
  selectDialogVisible.value = true;
}

const viewStore = useSampleViewStore();
const {
  removeView,
} = viewStore;


const resourceStore = useViewResourcesStore();
const {selectedResources, setViewResources, registerConfigCallback, unregisterConfigCallback} = resourceStore;
const resources = selectedResources(props.componentKey);
const juiceboxResources = ref<JuiceBoxConfig | null>(null);

const selection = (event) => {
  if (event.length > 2) {
    alert("Too Many")
    return;
  }

  setViewResources(props.componentKey, event)
  selectDialogVisible.value = false;
}

const buildUrl = usePartialContentUrl();

const config = ref<JuiceBoxConfig | null>()

watch(currentGenomicPosition, async () => {
  // const WINDOW = 10000;
  //
  const chr = currentGenomicPosition.value.chrom;
  let start = Number(currentGenomicPosition.value.start);
  let end = Number(currentGenomicPosition.value.end);
  //
  // let range = Math.abs(end - start)
  // if (range < WINDOW) {
  //   let additionalBp = Math.floor((WINDOW - range) / 2)
  //
  //   start = start - additionalBp
  //   end = end + additionalBp
  // }

  await browser.goto(chr, Number(start), Number(end), chr, Number(start), Number(end))
})

const clear = async () => {
  juiceboxResources.value = null;
}

type LocusChange = {
  state: {
    chr1: number
    chr2: number
    zoom: number
    x: number
    y: number
    pixelSize: number
    normalization: string
    locus: {
      x: {
        chr: string
        start: number
        end: number
      }
      y: {
        chr: string
        start: number
        end: number
      }
    }
  }
}

let debounceTimer: ReturnType<typeof setTimeout> | null = null;
let lastGenomicPosition = {
  chr: null,
  start: null,
  end: null,
};
const locusChange = (lc : LocusChange) => {
  const newGenomicPosition = lc.state.locus.x;
  if (newGenomicPosition === lastGenomicPosition) {
    return;
  }

  lastGenomicPosition = newGenomicPosition;
  if (debounceTimer) clearTimeout(debounceTimer);
  debounceTimer = setTimeout(() => {
    setCurrentGenomicPosition(newGenomicPosition.chr, newGenomicPosition.start, newGenomicPosition.end);
  }, 300);
};

let ro: ResizeObserver | null = null

const panel = ref<HTMLElement | null>(null);
type PanelDimensions = {
  width: number;
  height: number;
}
const panelDimensions = ref<PanelDimensions>({width: 0, height: 0});

let isInitializing = false;
const initializeJuiceBox = async () => {
  if (import.meta.client && config.value != null) {
    if (isInitializing) return;
    isInitializing = true;

    if (browser != null) {
      await destroy();
    }

    const juiceboxModule = await import('juicebox.js')
    const juicebox = juiceboxModule.default;

    browser = await juicebox.createBrowser(
        document.getElementById(props.componentKey),
        config.value
    );
    window['j'] = browser;
    browser.coordinator.externalCallbacks.onLocusChange.push(locusChange);
    // if (juiceboxResources.value != null) {
    //   browser.eventBus.subscribe('LocusChange', locusChange);
    // }

    isInitializing = false
    // resizeHicRoots(panel.value.getBoundingClientRect());
  }
}

onMounted(async () => {
  if (import.meta.client) {
    await initializeJuiceBox();

    ro = new ResizeObserver(([entry]) => {
      const cr = entry.contentRect
      resizeHicRoots(cr);
    })
    ro.observe(panel.value);
  }
})

const size = ref<Number>(600);
function resizeHicRoots(cr : DOMRectReadOnly): void {
  panelDimensions.value.width = cr.width
  panelDimensions.value.height = cr.height

  let width = cr.width
  let height = cr.height - 30

  size.value = Math.max(Math.min(width, height), 400);
  size.value = 600;

  const w = 480; // String(size.value)
  const h = 640; // String(size.value)

  console.log(`container H(px)=${height}, W(px)=${width}, Computed Size(px)=${size.value}`)

  if (!juiceboxRef.value) return
  const hicRoots = Array.from(juiceboxRef.value.querySelectorAll('.hic-root'))

  hicRoots.forEach(el => {
    (el as any).style.width = `${w}px`;
    (el as any).style.height = `${h}px`;
  })

  if (browser != null) {
    browser.repaint()
  }
}

const destroy = async () => {
  browser.unsyncSelf();
  browser.rootElement.remove();
  browser = null;

  // unsync listensers
  ro = null;
}
onBeforeUnmount(destroy)


watch(resources, async () => {
  if (!resources.value || resources.value.length === 0) {
    return null
  }

  const [main, control] = resources.value

  const jboxConfig: JuiceBoxConfig = {
    url: buildUrl(main),
    name: main.resourceName,
  }

  if (control) {
    jboxConfig.controlUrl = buildUrl(control)
    jboxConfig.controlName = control.resourceName
    jboxConfig.displayMode = "AOB"
  }

  config.value = jboxConfig;
  await initializeJuiceBox();
}, {
  deep: true,
  immediate: true
})

</script>

<style scoped>
.juice-box-app {
  display: flex;
  justify-content: center;
}

.panel {
  height: 100%;
  width: 100%;
}
</style>
