import DifferentialExpression from "@/components/DifferentialExpression.vue";

import { VIEW_NAMES } from '@shared/viewNames.ts';
import RnaSeqDifferentialPeak from "@/components/DifferentialPeak.vue";
import Igv from "@/components/Igv.client.vue";
import TandemRepeatsTransformer from "@/components/TandemRepeatsTransformer.vue";

export const viewComponentMap = {
    [VIEW_NAMES.DIFFERENTIAL_EXPRESSION]: DifferentialExpression,
    [VIEW_NAMES.DIFFERENTIAL_PEAK]: RnaSeqDifferentialPeak,
    [VIEW_NAMES.IGV]: Igv,
    [VIEW_NAMES.TandemRepeatsTransformer]: TandemRepeatsTransformer

};
