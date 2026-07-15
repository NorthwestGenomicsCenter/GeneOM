import { defineAsyncComponent, type Component } from 'vue';
// @ts-ignore
import { VIEW_NAMES } from '@shared/viewNames.ts';

export interface VisualizerDefinition {
    id: string;
    label: string;
    description?: string;
    icon?: string;
    component: Component;
}

export const visualizers: VisualizerDefinition[] = [
    {
        id: 'generic-table',
        label: 'Table',
        description: 'Tabular view with filtering and sorting',
        icon: 'pi pi-table',
        component: defineAsyncComponent(() => import('./GenericTable.vue')),
    },
    {
        id: VIEW_NAMES.DIFFERENTIAL_EXPRESSION,
        label: 'Differential Expression',
        icon: 'pi pi-chart-line',
        component: defineAsyncComponent(() => import('~/components/vizualizer/DifferentialExpression.vue')),
    },
    {
        id: VIEW_NAMES.DIFFERENTIAL_PEAK,
        label: 'Differential Peak',
        icon: 'pi pi-chart-bar',
        component: defineAsyncComponent(() => import('@/components/DifferentialPeak.vue')),
    },
    {
        id: VIEW_NAMES.IGV,
        label: 'IGV',
        icon: 'pi pi-align-left',
        component: defineAsyncComponent(() => import('~/components/vizualizer/Igv.client.vue')),
    },
    {
        id: VIEW_NAMES.TandemRepeatsTransformer,
        label: 'Tandem Repeats',
        icon: 'pi pi-sync',
        component: defineAsyncComponent(() => import('@/components/TandemRepeatsTransformer.vue')),
    },
    {
        id: 'ClusterGrammer',
        label: 'Clustergrammer',
        icon: 'pi pi-th-large',
        component: defineAsyncComponent(() => import('@/components/ClusterGrammer.client.vue')),
    },
    {
        id: 'JuiceBox',
        label: 'JuiceBox',
        icon: 'pi pi-box',
        component: defineAsyncComponent(() => import('@/components/vizualizer/JuiceBox.client.vue')),
    },
    {
        id: 'DifferentialExpressionTable',
        label: 'Differential Expression Table',
        icon: 'pi pi-table',
        component: defineAsyncComponent(() => import('@/components/vizualizer/DifferentialExpressionTable.vue')),
    },
    {
        id: 'TandemRepeatsGraph',
        label: 'Tandem Repeats Graph',
        icon: 'pi pi-chart-line',
        component: defineAsyncComponent(() => import('@/components/vizualizer/TandemRepeatsGraph.vue')),
    },
    {
        id: 'CallPeak',
        label: 'Call Peaks',
        icon: 'pi pi-chart-bar',
        component: defineAsyncComponent(() => import('@/components/vizualizer/CallPeakTable.vue')),
    },
    // add new visualizers here
];

export const visualizerMap: Record<string, VisualizerDefinition> =
    Object.fromEntries(visualizers.map((v) => [v.id, v]));

export const getVisualizer = (id: string): VisualizerDefinition | undefined =>
    visualizerMap[id];