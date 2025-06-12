export const VIEW_NAMES = {
    DIFFERENTIAL_EXPRESSION: 'differential-expression',
    DIFFERENTIAL_PEAK: 'differential-peak',
} as const;

export type ViewName = typeof VIEW_NAMES[keyof typeof VIEW_NAMES];