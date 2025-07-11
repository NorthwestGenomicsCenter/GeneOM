import DifferentialExpression from "@/components/DifferentialExpression.vue";

import { VIEW_NAMES } from '@shared/viewNames.ts';
import RnaSeqDifferentialPeak from "~/components/DifferentialPeak.vue";
// import Igv from "~/components/Igv.vue";

export const viewComponentMap = {
    [VIEW_NAMES.DIFFERENTIAL_EXPRESSION]: DifferentialExpression,
    [VIEW_NAMES.DIFFERENTIAL_PEAK]: RnaSeqDifferentialPeak,

};
