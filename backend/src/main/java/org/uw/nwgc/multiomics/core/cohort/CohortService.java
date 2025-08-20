package org.uw.nwgc.multiomics.core.cohort;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CohortService {

    private final CohortRepository cohortRepository;

    public CohortService(CohortRepository cohortRepository) {
        this.cohortRepository = cohortRepository;
    }

    public Optional<Cohort> getById(UUID id) {
        return cohortRepository.findById(id);
    }

    public Cohort create(Cohort cohort) {
        return cohortRepository.save(cohort);
    }

    public List<Cohort> getAll() {
        return cohortRepository.findAll();
    }


    private List<Cohort> buildTestProjects() {
        List<Cohort> projects = new ArrayList<>();

        // Define sample names
        String[] projectNames = {
                "altman_grc_rnaseq_6_phase3b",
                "anderson_grc_wgs_5",
                "aou_cvl_1",
                "aou_e2e_genotyping_2",
                "aou_pilot_fdablood_cvl_11",
                "armbrust_grc_rnaseq_20",
                "armbrust_grc_rnaseq_9",
                "bammler_grc_methylation_4",
                "bammler_grc_rnaseq_macaque_2",
                "bamshad_external_uwcmg_da_1",
                "bamshad_external_uwcmg_ga2l_1",
                "bamshad_external_uwcmg_ndd_1",
                "bamshad_uwcmg_amc_2",
                "bamshad_uwcmg_cdh_1",
                "bamshad_uwcrdr_cdh_wgssr_8",
                "bamshad_uwcrdr_eoho_wgspb_2",
                "bamshad_uwcrdr_fmf_wgsnp_3",
                "bamshad_uwcrdr_fmf_wgssr_6",
                "bamshad_uwcrdr_melas_wgsnp_1",
                "bamshad_uwcrdr_shs_wgsnp_1",
                "bamshad_uwcrdr_sn_wgssr_1",
                "bielas_external_uwcmg_ndd_1",
                "cherry_grc_miseq_1",
                "chung_uwcrdr_und_wgsnp_2",
                "cui_rnaseq_6",
                "doherty_grc_rnaseq_1",
                "doherty_uwcmg_res_3",
                "doorenbos_grc_exome_2",
                "eichler_grc_wgs_6",
                "glass_grc_genotyping_6",
                "graziano_uwcmg_mcanoid_1",
                "hakonarson_tm_to6_rnaseq_1",
                "hershberger_grc_dcm_21",
                "hershberger_grc_dcm_28",
                "janecke_uwcrdr_xecd_wgssr_4",
                "jaspan_grc_rnaseq_1_part2",
                "jenkins_uwcmg_htx_1",
                "kantaputra_grc_cm_wgssr_2",
                "karpen_grc_exome_6",
                "kelly_grc_rnaseq_3",
                "kihiu_grc_rnaseq_1",
                "kraemer_grc_celegans_2",
                "leal_uwcmg_id_1",
                "lingappa_grc_QC_1",
                "lood_grc_exome_1",
                "luquetti_grc_wgs_1",
                "mclaren_grc_iron_3",
                "meabon_grc_rnaseq_1",
                "mefford_uwcmg_dee_methylation_2",
                "morales_grc_rnaseq_depletion",
                "morales_RNAseq_4",
                "morris_grc_wgs_2",
                "neal_grc_rnaseq_1",
                "pgrn_seq_golden_reference_twist_1-1",
                "pharmhu_topmed_to5_rnaseq",
                "pvdomics_topmed_to6_rnaseq_2",
                "redline_nhlbi_phase3",
                "romitti_uwcmg_omph_2",
                "santos-cortez_grc_exome_4",
                "seqfirst_external_uwcrdr_und_wgspb_7",
                "stergachis_grc_fiberseq_9",
                "theusch_post_omni25_1",
                "udn_pnw_external_uwcmg_unknown_11",
                "udn_pnw_external_uwcrdr_unknown_103",
                "udn_pnw_external_uwcrdr_unknown_116",
                "udn_pnw_external_uwcrdr_unknown_82",
                "udn_pnw_external_uwcrdr_unknown_91",
                "udn_pnw_external_uwcrdr_unknown_97",
                "udn_ucla_external_uwcmg_fam369194_1",
                "udn_ucla_external_uwcmg_fam369194_rna_1",
                "udn_vu_external_uwcmg_fam210213_1",
                "west_grc_rnaseq_3_mouse",
                "whi_topmed_to6_rnaseq",
                "young_external_uwcmg_hbid_1"
        };

        // Generate samples
        for (String projectName : projectNames) {
            Cohort prj = new Cohort();
            prj.setName(projectName);
            projects.add(prj);
        }

        return projects;
    }

    public List<Cohort> fuzzyFindByName(String name, Integer limit) {
        return buildTestProjects().stream()
                .filter(prj -> prj.getName().toLowerCase().contains(name.toLowerCase()))
                .limit(limit)
                .toList();
    }

    public Optional<Cohort> update(UUID cohortId, CohortDTO cohortDto) {
        Optional<Cohort> cohortQuery = cohortRepository.findById(cohortId);
        if (cohortQuery.isPresent()) {
            Cohort cohort = cohortQuery.get();
            cohort.setName(cohortDto.getName());
            cohort.setDescription(cohortDto.getDescription());
            cohortRepository.save(cohort);

            return Optional.of(cohort);
        }

        return Optional.empty();
    }
}
