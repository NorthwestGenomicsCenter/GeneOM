Install Nuxi
============
`npx nuxi@latest init`

Creating an Admin User
======================
On the command line
gradlew admin --args="create-user admin 123456 science@uw.edu Science Guy"

Design
======
## Overview

This repository implements a multi‑omics web application built on a Spring Boot 3 backend and a Nuxt 3 frontend. It is designed for loose coupling between services and rapid rollout of new visualizations and data integrations.

### Backend

* **Modular Services**

    * **Auth**, **Genes**, **Annotations**, **Samples**, **Analyses**, **Search**, **Exceptions**
    * **Transformers**: encapsulate data‑format conversions and business logic
    * **Integrations**: connectors for external data sources
    * **DataResources Registry**: each entry binds

        1. a pointer to one data file
        2. the transformer class that prepares the data
        3. the name of the frontend component used to render it

* **Design Goals**

    * **Loose Coupling**: each service and transformer operates independently, communicating via well‑defined APIs and registry entries
    * **Extensibility**: new assays or integrations require only

        1. implementing a transformer
        2. registering its DataResource (file pointers + component name)

### Frontend

* **Dynamic Visualization**

    * Nuxt 3 components automatically discover and render every DataResource associated with a Sample or Analysis
    * Admin UI for managing DataResources and integration settings

* **Quick Development**

    * To add a new visualization:

        1. Create a Vue component
        2. Register it in the DataResources registry
        3. The system will fetch, transform, and display the new data without further wiring

This architecture ensures that backend and frontend remain decoupled, and that introducing new genomics assays or external data feeds is as simple as dropping in a new transformer and component.


```mermaid
flowchart LR
  %% Backend
  subgraph BE [Spring Boot 3 Backend]
    direction TB
    Auth[Auth Service]
    Genes[Genes Service]
    Annotations[Annotations Service]
    Exceptions[Exception Handlers]
    Samples[Samples Service]
    Analyses[Analyses Service]
    Search[Search Service]
    Transformers[Transformer Classes]
    Integrations[Integrations Module]
    DataResources[DataResources Registry]
  end

  %% Frontend
  subgraph FE [Nuxt 3 Frontend]
    direction TB
    AuthUI[Auth Components]
    SampleUI[Sample View Components]
    AnalysisUI[Analysis View Components]
    DRUI[DataResource Display Components]
    AdminUI[Admin Configuration UI]
    IntUI[Integration Pull UI]
  end

  %% External
  External[External Data Sources]

  %% Database
  DB[(Database)]

  %% Backend → Database
  Auth --> DB
  Genes --> DB
  Annotations --> DB
  Samples --> DB
  Analyses --> DB
  Search --> DB
  DataResources --> DB

  %% Backend internal flows
  DataResources -->|use| Transformers
  Integrations -->|populate| DataResources

  %% Integrations → External
  Integrations -->|fetch| External

  %% Frontend ⇄ Backend APIs
  AuthUI --REST API--> Auth
  SampleUI --REST API--> Samples
  AnalysisUI --REST API--> Analyses
  DRUI --REST API--> DataResources
  AdminUI --REST API--> Integrations
  IntUI --REST API--> Integrations
```