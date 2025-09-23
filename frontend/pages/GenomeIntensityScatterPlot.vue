<template>
  data: {{ data }}
  <canvas ref="canvasEl" style="width:100%;height:320px;"></canvas>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref } from 'vue'
import { Chart } from 'chart.js/auto'

export interface Props {
  yAxis?: string;
  url?: string;
}

const props = withDefaults(defineProps<Props>(), {
  yAxis: '-log(FDR)',
  url: "hello"
});

const data = await useApi(`/omic/visualize/scatter`);

// Order & lengths (GRCh38 primary assembly)
const chrOrder = [
  '1','2','3','4','5','6','7','8','9','10',
  '11','12','13','14','15','16','17','18','19','20','21','22','X','Y'
]
const chrLength: Record<string, number> = {
  '1': 248956422, '2': 242193529, '3': 198295559, '4': 190214555, '5': 181538259,
  '6': 170805979, '7': 159345973, '8': 145138636, '9': 138394717, '10': 133797422,
  '11': 135086622, '12': 133275309, '13': 114364328, '14': 107043718, '15': 101991189,
  '16': 90338345,  '17': 83257441,  '18': 80373285,  '19': 58617616,  '20': 64444167,
  '21': 46709983,  '22': 50818468,  'X': 156040895,  'Y': 57227415
}

const chrIndex = (chr: string) => chrOrder.indexOf(chr)

// Precompute cumulative starts and total genome length (sum of chosen chroms)
const starts: Record<string, number> = {}
let totalLen = 0
for (const c of chrOrder) {
  starts[c] = totalLen
  totalLen += chrLength[c]
}

// Human-readable bp (kb/Mb/Gb)
function humanBp(n: number) {
  if (n >= 1e9) return (n/1e9).toFixed(3).replace(/\.?0+$/,'') + ' Gb'
  if (n >= 1e6) return (n/1e6).toFixed(3).replace(/\.?0+$/,'') + ' Mb'
  if (n >= 1e3) return (n/1e3).toFixed(3).replace(/\.?0+$/,'') + ' kb'
  return String(n) + ' bp'
}

// Map { chr, pos, y } -> { x, y } using genome-wide coordinate
function toGenomewidePoint(d: { chr: string; pos: number; y: number }) {
  const i = chrIndex(d.chr)
  if (i < 0) return null
  const L = chrLength[d.chr]
  const p = Math.max(0, Math.min(d.pos, L))
  const x = starts[d.chr] + p
  return { x, y: d.y, _chr: d.chr, _pos: p }
}

// Example data
const raw = [
  { chr: '1', pos: 12_345_678, y: 3.2 },
  { chr: '1', pos: 200_000_000, y: 4.5 },
  { chr: '17', pos: 44_500_000, y: 2.1 },
  { chr: 'X', pos: 10_000_000, y: 5.0 },
  { chr: 'Y', pos: 5_000_000, y: 1.2 }
].map(toGenomewidePoint).filter(Boolean) as {x:number;y:number;_chr:string;_pos:number}[]

// Plugin: draw alternating chrom backgrounds, boundaries at starts[c], and centered labels
const chromosomeBands = {
  id: 'chromosomeBands',
  afterDraw(chart: any) {
    const { ctx, chartArea, scales } = chart
    const xScale = scales.x
    if (!xScale) return
    ctx.save()

    // Alternating background rectangles per chromosome (variable width)
    for (let i = 0; i < chrOrder.length; i++) {
      const c = chrOrder[i]
      const x0 = xScale.getPixelForValue(starts[c])
      const x1 = xScale.getPixelForValue(starts[c] + chrLength[c])
      if (i % 2 === 0) {
        ctx.fillStyle = 'rgba(0,0,0,0.03)'
        ctx.fillRect(x0, chartArea.top, x1 - x0, chartArea.bottom - chartArea.top)
      }
    }

    // Vertical boundaries at chromosome starts and genome end
    ctx.strokeStyle = 'rgba(0,0,0,0.15)'
    ctx.lineWidth = 1
    for (let i = 0; i < chrOrder.length; i++) {
      const c = chrOrder[i]
      const x = xScale.getPixelForValue(starts[c])
      ctx.beginPath()
      ctx.moveTo(x, chartArea.top)
      ctx.lineTo(x, chartArea.bottom)
      ctx.stroke()
    }
    // Final boundary at the end of the last chromosome
    const xEnd = xScale.getPixelForValue(totalLen)
    ctx.beginPath()
    ctx.moveTo(xEnd, chartArea.top)
    ctx.lineTo(xEnd, chartArea.bottom)
    ctx.stroke()

    // Labels at chromosome centers
    ctx.fillStyle = 'rgba(0,0,0,0.8)'
    ctx.font = '12px sans-serif'
    ctx.textAlign = 'center'
    ctx.textBaseline = 'top'
    for (let i = 0; i < chrOrder.length; i++) {
      const c = chrOrder[i]
      const mid = starts[c] + chrLength[c] / 2
      const px = xScale.getPixelForValue(mid)
      ctx.fillText(c, px, chartArea.bottom + 6)
    }

    ctx.restore()
  }
}

const canvasEl = ref<HTMLCanvasElement|null>(null)
let chart: Chart | null = null

onMounted(() => {
  chart = new Chart(canvasEl.value!, {
    type: 'scatter',
    data: {
      datasets: [{
        label: 'Genomic points',
        data: raw,          // [{x, y, _chr, _pos}, ...] in genome-wide coords
        pointRadius: 3,
        showLine: false
      }]
    },
    options: {
      animation: false,
      parsing: false, // x/y provided already
      layout: { padding: { bottom: 24 } }, // room for chromosome labels
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label(ctx) {
              const d = ctx.raw as any
              return `chr${d._chr}:${humanBp(d._pos)} → y=${ctx.parsed.y}`
            },
            footer() {
              // Show genome-wide x as well (optional)
              return ''
            }
          }
        }
      },
      scales: {
        x: {
          type: 'linear',
          min: 0,
          max: totalLen,
          grid: { display: false },
          ticks: { display: false },
          title: { display: false, text: 'Chromosome' }
        },
        y: {
          type: 'linear',
          title: { display: true, text: props.yAxis }
        }
      }
    },
    plugins: [chromosomeBands]
  })
})

onBeforeUnmount(() => chart?.destroy())
</script>
