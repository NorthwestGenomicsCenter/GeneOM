export const VIEW_NAMES = {
    DIFFERENTIAL_EXPRESSION: 'differential-expression',
    DIFFERENTIAL_PEAK: 'differential-peak',
    IGV: 'igv',
} as const;

export type ViewName = typeof VIEW_NAMES[keyof typeof VIEW_NAMES];