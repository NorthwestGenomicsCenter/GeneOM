<template>
  <section ref="panel" class="panel">
    <CommonHeader
      title="Juice Box"
      @close="removeView(componentKey)"
      @settings="onSettings">
    </CommonHeader>
    <div id="app-container-juice-box"></div>
  </section>
</template>

<script setup lang="ts">
import {useSampleViewStore} from "~/composables/useSampleViewStore";
import {Skeleton} from "primevue";

const configOld = {
  "url": "https://hicfiles.s3.amazonaws.com/hiseq/gm12878/dilution/combined.hic",
}

const configLocal = {
  "url": "http://localhost:3000/api/omic/igv/36ff13e7-8dd2-477e-984a-696d3342a7b8/combined.hic",
}

const config =
    {
      "backgroundColor": "255,255,255",
      "url": "https://www.encodeproject.org/files/ENCFF876OWE/@@download/ENCFF876OWE.hic",
      "state": "0,0,0,0,0,640,640,1.151893315580228,NONE",
      "colorScale": "R:5:1.6094379124341003,255,0,0:1.6094379124341003,0,0,255",
      "nvi": "1538692704,47429",
      "controlUrl": "https://www.encodeproject.org/files/ENCFF219YOB/@@download/ENCFF219YOB.hic",
      "controlName": "Hi-C on A549 cell line treated with 100 nM dexamethasone for 12 hours.",
      "displayMode": "AOB",
      "controlNvi": "2859767211,26979"
    };

let browser = null;

const onSettings = async () => {
  alert("Hello Settings for Juice Box");
}

const props = defineProps<{
  componentKey: any
}>();

const viewStore = useSampleViewStore();
const { selectedViews, layoutMap } = storeToRefs(viewStore)
const { removeView } = viewStore;

const layout = computed(() => layoutMap.value.get(props.componentKey))
watch(layout, () => {
  const size = Math.min(width.value, height.value - 250);

  browser.layoutController.doLayoutWithRootContainerSize({
        width: size,
        height: size
      }
  );
})

const goto = async () => {
  const locus = browser.parseLocusString('17:44,100,000-44,600,000');
  await browser.goto(locus.chr, locus.start, locus.end, locus.chr, locus.start, locus.end, 10000)
}

const locusChange = (e) => {
  console.table(browser.state)
  console.log(e)
  console.log(`Now at ${e.chr1}:${e.x} vs ${e.chr2}:${e.y}`);
};

const panel = ref<HTMLElement|null>(null);
let ro: ResizeObserver | null = null
const width = ref(0);
const height = ref(0);

onMounted(async () => {
  if (import.meta.client) {
    const juiceboxModule = await import('juicebox.js')
    const juicebox = juiceboxModule.default;

    browser = await juicebox.init(document.getElementById("app-container-juice-box"), config);
    window['juicebox'] = browser;
    console.log(`${browser.id} is good to go`)
    browser.eventBus.subscribe('LocusChange', locusChange);
    // browser.layoutController.doLayoutWithRootContainerSize({ width: 320, height: 320 });

    ro = new ResizeObserver(([entry]) => {
      const cr = entry.contentRect
      width.value = cr.width
      height.value = cr.height
    })
    ro.observe(panel.value)
  }
})

</script>

<style scoped>
.panel {
  height: 100%;
  width: 100%;
}
</style>