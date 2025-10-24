import DifferentialExpression from "@/components/DifferentialExpression.vue";

import { VIEW_NAMES } from '@shared/viewNames.ts';
import RnaSeqDifferentialPeak from "@/components/DifferentialPeak.vue";
import Igv from "@/components/Igv.client.vue";
import TandemRepeatsTransformer from "@/components/TandemRepeatsTransformer.vue";
import ClusterGrammer from "@/components/ClusterGrammer.client.vue";
import JuiceBox from "@/components/vizualizer/JuiceBox.client.vue";
// import DifferentialPeak from '@/components/vizualizer/DifferentialPeak.vue'
import DifferentialExpressionTable from '@/components/vizualizer/DifferentialExpressionTable.vue'
import TandemRepeatsGraph from '@/components/vizualizer/TandemRepeatsGraph.vue'

export const viewComponentMap = {
    [VIEW_NAMES.DIFFERENTIAL_EXPRESSION]: DifferentialExpression,
    [VIEW_NAMES.DIFFERENTIAL_PEAK]: RnaSeqDifferentialPeak,
    [VIEW_NAMES.IGV]: Igv,
    [VIEW_NAMES.TandemRepeatsTransformer]: TandemRepeatsTransformer,
    "ClusterGrammer": ClusterGrammer,
    "JuiceBox": JuiceBox,
    "DifferentialExpressionTable": DifferentialExpressionTable,
    "TandemRepeatsTransformer": TandemRepeatsTransformer,
    "TandemRepeatsGraph": TandemRepeatsGraph,
    
};
