package org.uw.nwgc.multiomics.core.analysis;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.uw.nwgc.multiomics.core.analysis.dto.*;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.sample.SampleService;
import org.uw.nwgc.multiomics.core.sample.dto.SampleDTO;
import org.uw.nwgc.multiomics.core.user.CurrentUserResolver;
import org.uw.nwgc.multiomics.core.user.User;
import org.uw.nwgc.multiomics.core.user.UserDTO;
import org.uw.nwgc.multiomics.core.user.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnalysisService {
    private final AnalysisMapper analysisMapper;
    private final AnalysisRepository analysisRepository;
    private final CurrentUserResolver currentUserResolver;
    private final SampleService sampleService;
    private final UserService userService;

    public AnalysisService(
            AnalysisMapper analysisMapper,
            AnalysisRepository analysisRepository,
            CurrentUserResolver currentUserResolver,
            SampleService sampleService,
            UserService userService
    ) {
        this.analysisMapper = analysisMapper;
        this.analysisRepository = analysisRepository;
        this.currentUserResolver = currentUserResolver;
        this.sampleService = sampleService;
        this.userService = userService;
    }

    Analysis save(Analysis analysis) {
        return analysisRepository.save(analysis);
    }

    public Optional<Analysis> findById(UUID id) {
        return analysisRepository.findById(id);
    }

    Collection<Analysis> getAll() {
        return analysisRepository.findAll();
    }

    Analysis createNew(CreateAnalysisDTO dto, UserDetails userDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = currentUserResolver.resolve((UserDetails) auth.getPrincipal());

        Analysis analysis = new Analysis();
        analysis.setName(dto.getName());
        analysis.setDescription(dto.getDescription());
        analysis.setLastModified(LocalDateTime.now());
        analysis.setReferenceGenome(ReferenceGenome.hg38); // default genome
        analysis.setUsers(Set.of(user));
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

    protected List<AvailableUserAnalysisDTO> getAvailableUserAnalysisDTOs(UserDetails user) {
        List<Analysis> analyses;
        if(User.isAdmin(user)) {
            analyses = analysisRepository.findAll();
        } else {
            analyses = analysisRepository.findByUsersUsernameOrderByLastModifiedDesc(user.getUsername());
        }

        return analyses
                .stream()
                .map(AvailableUserAnalysisDTO::new)
                .toList();
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

    public RecentAnalysisDTO getRecent() {
        RecentAnalysisDTO dto = new RecentAnalysisDTO();
        dto.setTotalAnalyses(analysisRepository.count());

        analysisRepository
                .findTop5ByOrderByLastModifiedDesc()
                .forEach(dto::add);

        return dto;
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

    public Analysis addUsers(AddAnalysisUsersDTO dto) {
        Optional<Analysis> analysisQuery = findById(dto.getAnalysisId());
        if (analysisQuery.isEmpty()) {
            return null;
        }

        Analysis analysis = analysisQuery.get();
        List<User> users = userService.getAllUsersInList(dto.getUsers().stream().map(UserDTO::getId).toList());
        analysis.getUsers().clear();
        analysis.getUsers().addAll(users);
        analysisRepository.save(analysis);
        return analysis;
    }

    /***
     * Update the analysis on the edit analysis page, and save the results to the database.
     * @param analysis An analysis
     * @param dto UpdateAnalysisDTO to update the metadata for an analysis
     * @return The new saved analysis
     */
    public Analysis updateAnalysis(Analysis analysis, UpdateAnalysisDTO dto) {
        analysis.setName(dto.getName());
        analysis.setDescription(dto.getDescription());
        analysis.setReferenceGenome(ReferenceGenome.valueOf(dto.getReferenceGenome()));

        analysis.setLastModified(LocalDateTime.now());

        return analysisRepository.save(analysis);
    }

    public void deleteSnapshotPosition(UUID spanshotId) {

    }
}
