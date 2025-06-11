<template>
  Gene Names and Relations from <a href="https://www.genenames.org/">HUGO gene nomenclature committee</a>

  <Accordion multiple>
    <h3>Genes</h3>
    <template v-for="gene in store.globalNav.genes" :key="gene">
      <AccordionPanel :value="gene">
        <AccordionHeader>{{ gene }}</AccordionHeader>
        <AccordionContent>
          <p
              @click="store.changeGene(gene)">
            {{ gene }}
            <Button label="Search Related" small @click="findRelatedGenes(gene)"/>
          </p>
        </AccordionContent>
      </AccordionPanel>
    </template>
  </Accordion>

  <h3>Related Genes</h3>
  <p
      v-for="gene in related" :key="gene.id">
    <Tag @click="store.addGene(gene.symbol)">{{ gene.symbol }}</Tag>
    {{ gene.name }}
  </p>
</template>

<script setup>
import {ref} from 'vue'
import {useGlobalStore} from "~/composables/useGlobal.ts";

import Accordion from 'primevue/accordion';
import AccordionPanel from 'primevue/accordionpanel';
import AccordionHeader from 'primevue/accordionheader';
import AccordionContent from 'primevue/accordioncontent';

const store = useGlobalStore();

const related = ref([]);

const findRelatedGenes = async (name) => {
  related.value = await $fetch(`/api/gene/related/${name}`)
}
</script>

<style scoped>

</style>