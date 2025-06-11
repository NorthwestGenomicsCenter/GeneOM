<template>
  <div class="sample-card">
    <span class="header-line">
      <Tag>{{ sample.name }}</Tag>
      <div class="sample-lims">
        <strong>LIMS ID:</strong> {{ sample.externalLimsId }}
      </div>
    </span>
    <p class="sample-project"><strong>Project:</strong> {{ sample.externalProjectName }}</p>
    <p class="sample-description"><strong>Description:</strong> {{ sample.description }}</p>
  </div>
</template>

<script setup lang="ts">
interface Sample {
  id: string;
  name: string;
  externalLimsId: string;
  externalProjectName: string;
  description: string;
  tags: Tag[];
  users: User[];
  omicResources?: Omicresource[];
}

interface Omicresource {
  omicResourceId: string;
  sampleId: string;
  path: string;
  igvTrack?: boolean;
  visualizers: string[];
}

interface User {
  id: number;
  userName: string;
  firstName: string;
  lastName: string;
  email: string;
  permissions: string[];
}

interface Tag {
  id: string;
  label: string;
  value: string;
}

const { sample } = defineProps<{ sample: Sample }>();
</script>

<style scoped>
.header-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.875rem;
}

.sample-card {
  display: inline-block;
  vertical-align: top;
  min-width: 60ch;       /* Minimum width of ~60 characters */
  max-width: 80ch;       /* Maximum width of ~80 characters */
  border: 1px solid #e0e0e0;
  border-radius: 0.5rem;
  padding: 0.75rem 1rem;
  margin: 0.25rem;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow-wrap: break-word;
}
.sample-name {
  font-size: 1rem;
  font-weight: 700;      /* Bold the title text */
  margin: 0;
}
.sample-lims,
.sample-project,
.sample-description {
  font-size: 0.875rem;
  margin: 0.25rem 0 0;
}
</style>