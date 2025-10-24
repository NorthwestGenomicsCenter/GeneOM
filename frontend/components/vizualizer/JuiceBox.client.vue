<template>
  <section ref="panel" class="panel">
    <CommonHeader
        title="Juice Box"
        @close="removeView(componentKey)"
        @settings="onSettings">
    </CommonHeader>
    <div :id="componentKey" class="juice-box-app" ref="juiceboxRef"></div>
    <!--    <template v-if="juiceboxResources == null">-->
    <!--      <Button label="Add Resources" @click="addResource"/>-->
    <!--    </template>-->
    <!--    <template v-else>-->
    <!--      <div id="app-container-juice-box" ref="juiceboxRef"></div>-->
    <!--    </template>-->
    <!--    <Button label="Goto" @click="goto" />-->
    <!--    <Button label="Clear" @click="clear" />-->

  </section>

  <Dialog header="Select Samples" v-model:visible="selectDialogVisible" modal>
    <ViewerResourceTable
        :componentKey="componentKey"
        :tableData="analysis.resources"
        :filetypes="['bam', 'hic']"
        @select-resources="selection"/>
  </Dialog>
</template>

<script setup lang="ts">
import {computed} from 'vue'
import {useSampleViewStore} from "~/composables/useSampleViewStore";
import {Skeleton} from "primevue";

const configOld = {
  "url": "https://hicfiles.s3.amazonaws.com/hiseq/gm12878/dilution/combined.hic",
}

const configLocal = {
  "url": "http://localhost:3000/api/omic/igv/36ff13e7-8dd2-477e-984a-696d3342a7b8/combined.hic",
}

// const config =
//     {
//       "backgroundColor": "255,255,255",
//       "url": "https://www.encodeproject.org/files/ENCFF876OWE/@@download/ENCFF876OWE.hic",
//       "state": "0,0,0,0,0,640,640,1.151893315580228,NONE",
//       "colorScale": "R:5:1.6094379124341003,255,0,0:1.6094379124341003,0,0,255",
//       "nvi": "1538692704,47429",
//       "controlUrl": "https://www.encodeproject.org/files/ENCFF219YOB/@@download/ENCFF219YOB.hic",
//       "controlName": "Hi-C on A549 cell line treated with 100 nM dexamethasone for 12 hours.",
//       "displayMode": "AOB",
//       "controlNvi": "2859767211,26979"
//     };

let browser = null;

// const onSettings = async () => {
//   alert("Hello Settings for Juice Box");
// }

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
}

const selectDialogVisible = ref<boolean>(false);
const onSettings = async () => {
  selectDialogVisible.value = true;
}

const viewStore = useSampleViewStore();
const {layoutMap, analysis} = storeToRefs(viewStore)
const {
  removeView,
} = viewStore;


const resourceStore = useViewResourcesStore();
const {
  getViewResources,
  setViewResources,
  clearViewResources
} = resourceStore;
const resources = getViewResources(props.componentKey);

const layout = computed(() => layoutMap.value.get(props.componentKey))
watch(layout, () => {
  const size = Math.min(width.value, height.value - 250);

  browser.layoutController.doLayoutWithRootContainerSize({
        width: size,
        height: size
      }
  );
})

const juiceboxResources = ref<JuiceBoxConfig | null>(null);

const selection = (event) => {
  if (event.length > 2) {
    alert("Too Many")
    return;
  }

  setViewResources(props.componentKey, event)
}

const buildUrl = usePartialContentUrl();

const config = ref<JuiceBoxConfig | null>()

const goto = async () => {
  const locus = browser.parseLocusString('17:44,100,000-44,600,000');
  await browser.goto(locus.chr, locus.start, locus.end, locus.chr, locus.start, locus.end, 10000)
}

const clear = async () => {
  clearViewResources(props.componentKey)
  juiceboxResources.value = null;
}

const locusChange = (e) => {
  console.table(browser.state)
  console.log(e)
  console.log(`Now at ${e.chr1}:${e.x} vs ${e.chr2}:${e.y}`);
};

const panel = ref<HTMLElement | null>(null);
let ro: ResizeObserver | null = null
const width = ref(0);
const height = ref(0);

const initializeJuiceBox = async () => {
  if (import.meta.client && config.value != null) {
    if (browser != null) {
      await destroy();
    }

    const juiceboxModule = await import('juicebox.js')
    const juicebox = juiceboxModule.default;

    browser = await juicebox.init(document.getElementById(props.componentKey), config.value);
    // window['juicebox'] = browser;

    if (juiceboxResources.value != null) {
      browser.eventBus.subscribe('LocusChange', locusChange);
    }

  }
}

onMounted(async () => {
  if (import.meta.client) {
    await initializeJuiceBox();

    ro = new ResizeObserver(([entry]) => {
      const cr = entry.contentRect
      width.value = cr.width
      height.value = cr.height
    })
    ro.observe(panel.value)
  }
})

const destroy = async () => {
  browser.unsyncSelf();
  // browser.rootElement.remove();
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
