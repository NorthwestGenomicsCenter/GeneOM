```mermaid
graph TD
    Core["Core Genomics Browser"]
    Transcriptomics["Transcriptomics"]
    Epigenomics["Epigenomics"]
    Proteomics["Proteomics"]
    Metabolomics["Metabolomics"]
    OtherOmics["Other Omics Data"]

    Core --> Transcriptomics
    Core --> Epigenomics
    Core --> Proteomics
    Core --> Metabolomics
    Core --> OtherOmics
```