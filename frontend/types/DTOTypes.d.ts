/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2025-06-17 13:31:34.

interface SampleViewDTO {
    technology: string;
    viewName: string;
}

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

interface LoginResponseDTO {
    accessToken: string;
    refreshToken: string;
    type: string;
    id: number;
    username: string;
    roles: string[];
}

interface CohortDTO {
    id: string;
    name: string;
    description: string;
    count: number;
}

interface FinderFileDTO {
    adapter: string;
    type: string;
    path: string;
    lastModified: number;
    fileSize: number;
    basename: string;
    extension: string;
    extraMetadata: any[];
    visibility: string;
    storage: string;
}

interface FinderFolderDTO {
    adapter: string;
    basename: string;
    path: string;
}

interface FinderMessageDTO {
    adapter: string;
    dirname: string;
    files: FinderFileDTO[];
    storages: string[];
}

interface SampleFileDTO {
    sampleId: string;
    path: string;
}

interface AddOmicsResourceDTO {
    sampleId: string;
    path: string;
    visualizers: string[];
    igvTrack: boolean;
}

interface OmicResourceDTO {
    omicResourceId: string;
    sampleId: string;
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
