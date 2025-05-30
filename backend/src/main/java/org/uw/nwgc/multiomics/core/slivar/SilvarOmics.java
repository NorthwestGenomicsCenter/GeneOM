package org.uw.nwgc.multiomics.core.slivar;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class SilvarOmics {

    Map<String, BiConsumer<SilvarOmics, String>> setterMap = new HashMap<>();

    {
        setterMap.put("#mode", null);
        setterMap.put("family_id", null);
        setterMap.put("sample_id", null);
        setterMap.put("chr:pos:ref:alt", null);
        setterMap.put("genotype(sample,dad,mom)", null);
        setterMap.put("gnomadV4joint_maxFAF95", null);
        setterMap.put("gnomadV4joint_FAF95_all", null);
        setterMap.put("gnomadV4joint_nHomAlt", null);
        setterMap.put("highest_impact_order", null);
        setterMap.put("SPLICEAI_MAXSCORE", null);
        setterMap.put("gene", null);
        setterMap.put("highest_impact", null);
        setterMap.put("depths(sample,dad,mom)", null);
        setterMap.put("allele_balance(sample,dad,mom)", null);
        setterMap.put("gene_impact_transcript_CANONICAL_CADD_PHRED_REVEL_SCREEN_CLINVAR_CLNSIG_CLINVAR_CLNDN_LOEUF_UPPER_MOEUF_UPPER_UNEECON_SHET_POSTMEAN_SPLICEAI_PRED_DP_AG_SPLICEAI_PRED_DP_AL_SPLICEAI_PRED_DP_DG_SPLICEAI_PRED_DP_DL_SPLICEAI_PRED_DS_AG_SPLICEAI_PRED_DS_AL_SPLICEAI_PRED_DS_DG_SPLICEAI_PRED_DS_DL_SPLICEAI_PRED_SYMBOL_5UTR_CONSEQUENCE_5UTR_ANNOTATION_SPLICEREGION_CLINVAR_GIP_CLNSIG_CLINVAR_GIP_CLNDN_SYSERROR_AF_lowCI_AM_PATHOGENICITY_AM_CLASS_SPLICEVAULT_TOP_EVENTS_SPLICEVAULT_SITE_SAMPLE_COUNT_SPLICEVAULT_SITE_MAX_DEPTH_SPLICEVAULT_OUT_OF_FRAME_EVENTS_SPLICEVAULT_SITE_POS_SPLICEVAULT_SITE_TYPE_SPLICEVAULT_SPLICEAI_DELTA", null);
    }


}
