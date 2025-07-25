export const VIEW_NAMES = {
    DIFFERENTIAL_EXPRESSION: 'differential-expression',
    DIFFERENTIAL_PEAK: 'differential-peak',
    IGV: 'Igv',
    TandemRepeatsTransformer: 'TandemRepeatsTransformer',
} as const;

export type ViewName = typeof VIEW_NAMES[keyof typeof VIEW_NAMES];