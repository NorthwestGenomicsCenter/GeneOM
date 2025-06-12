/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2025-06-11 13:59:25.

interface IsoformCsvDTO {
    Isoform_PBid: string;
    associated_gene: string;
    Sample: string;
    Cyclo_TPM: number;
    Noncyclo_TPM: number;
    test_statistic: number;
    z_score: number;
    NormalizedFractionDifference: number;
    OMIM_phenotypes: string;
    rank_top_99_9_percentile: number;
    rank_top_99_5_percentile: number;
    rank_top_99_percentile: number;
    rank_top_98_percentile: number;
    rank_top_95_percentile: number;
}

interface CohortDTO {
    id: string;
    name: string;
    description: string;
    count: number;
}

interface SampleFileDTO {
    sampleId: string;
    path: string;
}

interface OmicResourceDTO {
    omicResourceId: string;
    sampleid: string;
    integrationSource: string;
    path: string;
    igvTrack: boolean;
    visualizers: string[];
}

interface SampleDTO {
    id: string;
    name: string;
    integrationSource: string;
    externalLimsId: string;
    externalProjectName: string;
    description: string;
    tags: SampleTagDTO[];
    users: UserDTO[];
    omicResources: OmicResourceDTO[];
}

interface SearchResponseDTO {
    label: string;
    items: any[];
}

interface UserDTO {
    id: number;
    userName: string;
    firstName: string;
    lastName: string;
    email: string;
    permissions: string[];
}

interface SampleTagDTO {
    id: string;
    label: string;
    value: string;
}
