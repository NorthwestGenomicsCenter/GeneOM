package org.uw.nwgc.multiomics.core.analysis;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.uw.nwgc.multiomics.core.analysis.dto.AddAnalysisSampleDTO;
import org.uw.nwgc.multiomics.core.analysis.dto.AnalysisEditDTO;
import org.uw.nwgc.multiomics.core.analysis.dto.CreateAnalysisDTO;
import org.uw.nwgc.multiomics.core.analysis.dto.CreateCustomReferenceGenomeDTO;
import org.uw.nwgc.multiomics.core.analysis.dto.RecentAnalysisDTO;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.sample.SampleDTO;
import org.uw.nwgc.multiomics.core.sample.SampleService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnalysisService {
    private final AnalysisRepository analysisRepository;
    private final AnalysisMapper analysisMapper;
    private final SampleService sampleService;

    public AnalysisService(AnalysisRepository analysisRepository, AnalysisMapper analysisMapper, SampleService sampleService) {
        this.analysisRepository = analysisRepository;
        this.analysisMapper = analysisMapper;
        this.sampleService = sampleService;
    }

    Analysis save(Analysis analysis) {
        return analysisRepository.save(analysis);
    }

    Optional<Analysis> findById(UUID id) {
        return analysisRepository.findById(id);
    }

    Collection<Analysis> getAll() {
        return analysisRepository.findAll();
    }

    Analysis createNew(CreateAnalysisDTO dto) {
        Analysis analysis = new Analysis();
        analysis.setName(dto.getName());
        analysis.setDescription(dto.getDescription());
        analysis.setLastModified(LocalDateTime.now());
        analysis.setReferenceGenome(ReferenceGenome.hg38); // default genome
        return analysisRepository.save(analysis);
    }

    boolean updateLayout(UUID id, String newLayout) {
        Optional<Analysis> analysisQuery = analysisRepository.findById(id);
        if (analysisQuery.isPresent()) {
            Analysis analysis = analysisQuery.get();
            if (newLayout.equals(analysis.getLayout())) {
                return false;
            }
            analysis.setLayout(newLayout);
            analysisRepository.save(analysis);
        }

        return true;
    }

    boolean updateSelectedResources(UUID id, String selectedResources) {
        Optional<Analysis> analysisQuery = analysisRepository.findById(id);
        if (analysisQuery.isPresent()) {
            Analysis analysis = analysisQuery.get();
//            if (newLayout.equals(analysis.getLayout())) {
//                return false;
//            }
            analysis.setSelectedResources(selectedResources);
            analysisRepository.save(analysis);
        }

        return true;
    }

    void delete(UUID id) {
        analysisRepository.deleteById(id);
    }

    public AnalysisEditDTO getEditDTO(UUID id) {
        Analysis analysis = analysisRepository.findById(id).orElse(null);
        if (analysis == null) {
            throw new EntityNotFoundException("Analysis with id " + id + " not found");
        }

        return analysisMapper.toEditDTO(analysis);
    }

    public void addSamples(AddAnalysisSampleDTO dto) {
        UUID id = dto.getAnalysisId();
        Analysis analysis = analysisRepository.findById(id).orElse(null);
        if (analysis == null) {
            throw new EntityNotFoundException("Analysis with id " + id + " not found");
        }

        List<UUID> sampleIds = dto.getSamples()
                .stream()
                .map(SampleDTO::getId)
                .toList();

        Optional<List<Sample>> samples = sampleService.findByIds(sampleIds);
        if (samples.isEmpty()) {
            throw new EntityNotFoundException("Samples are not found to add to analysis: " + sampleIds.stream().map(UUID::toString).collect(Collectors.joining(",")));
        }

        analysis.getSamples().addAll(samples.get());
        analysisRepository.save(analysis);
    }

    public Collection<RecentAnalysisDTO> getRecent() {
        return analysisRepository.findTop5ByOrderByLastModifiedDesc()
                .stream().map(a -> new RecentAnalysisDTO(a.getId(), a.getName())).collect(Collectors.toList());
    }

    public Analysis createNewCustomReferenceGenome(UUID id, CreateCustomReferenceGenomeDTO dto) {
        Optional<Analysis> analysisQuery = analysisRepository.findById(id);
        if (analysisQuery.isPresent()) {
            Analysis analysis = analysisQuery.get();
            analysis.setCustomReference(dto.createCustomReference());
            analysisRepository.save(analysis);

            return analysis;
        }

        return null;
    }
}
