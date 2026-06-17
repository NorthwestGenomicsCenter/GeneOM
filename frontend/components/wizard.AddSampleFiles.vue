<template>
  <Stepper value="1">
    <StepList>
      <Step value="1">Add Files</Step>
      <Step value="2">Configure Visualizers</Step>
      <Step value="3">Header III</Step>
    </StepList>
    <StepPanels>
      <StepPanel v-slot="{ activateCallback }" value="1">
        <FileUpload
            name="file"
            :url="`${baseUrl}/api/omic/upload`"
            :multiple="true"
            :maxFileSize="1000000000"
            @beforeUpload="beforeUpload"
            @upload="afterUpload"
            @error="uploadError" >
          <template #empty>
            <p>Drag and drop files to here to upload.</p>
          </template>
        </FileUpload>
        <br />
        <VueFinder
            id="vf"
            :features="[]"
            :request="`${baseUrl}/api/vuefinder/search/`"
            @select="handleSelectEvent" />
      </StepPanel>
      <StepPanel v-slot="{ activateCallback }" value="2">
        <div class="flex flex-col h-48">
          <div class="border-2 border-dashed border-surface-200 dark:border-surface-700 rounded bg-surface-50 dark:bg-surface-950 flex-auto flex justify-center items-center font-medium">Content II</div>
        </div>
        <div class="flex pt-6 justify-between">
          <Button label="Back" severity="secondary" icon="pi pi-arrow-left" @click="activateCallback('1')" />
          <Button label="Next" icon="pi pi-arrow-right" iconPos="right" @click="activateCallback('3')" />
        </div>
      </StepPanel>
      <StepPanel v-slot="{ activateCallback }" value="3">
        <div class="flex flex-col h-48">
          <div class="border-2 border-dashed border-surface-200 dark:border-surface-700 rounded bg-surface-50 dark:bg-surface-950 flex-auto flex justify-center items-center font-medium">Content III</div>
        </div>
        <div class="pt-6">
          <Button label="Back" severity="secondary" icon="pi pi-arrow-left" @click="activateCallback('2')" />
        </div>
      </StepPanel>
    </StepPanels>
  </Stepper>
</template>

<script setup lang="ts">
import type {FileUploadBeforeUploadEvent, FileUploadErrorEvent} from "primevue";
import {useToast} from "#imports";

// -- Component Setup ---------------------------------------------------------
interface Props {
  sampleId: string
}
const props = defineProps<Props>();

const toast = useToast();

let baseUrl = ref(useRuntimeConfig().app.baseURL);
if (baseUrl.value === "/") {
  baseUrl.value = "";
}

const beforeUpload = async (e : FileUploadBeforeUploadEvent) => {
  e.formData.set("sampleId", props.sampleId);
}

const afterUpload = async () => {
  // await refresh();
}

const uploadError = async (e : FileUploadErrorEvent) => {
  toast.add({
    severity: 'error',
    summary: "Upload Error",
    detail: e.xhr.response,
    life: 15000
  })
}

const selectedFiles = ref<FinderFileDTO[]>([]);
const handleSelectEvent = (selection : FinderFileDTO[]) => {
  selectedFiles.value = selection
}

</script>

<style scoped>

</style>