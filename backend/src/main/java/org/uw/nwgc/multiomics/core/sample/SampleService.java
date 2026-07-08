package org.uw.nwgc.multiomics.core.sample;

import jakarta.persistence.criteria.Join;
import jakarta.security.auth.message.AuthException;
import jakarta.transaction.Transactional;
import org.hibernate.ObjectNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.uw.nwgc.multiomics.core.resources.OmicResource;
import org.uw.nwgc.multiomics.core.sample.dto.AddExternalSampleResourcesDTO;
import org.uw.nwgc.multiomics.core.sample.dto.CreateSampleDTO;
import org.uw.nwgc.multiomics.core.sample.dto.CreateSampleFromExternalDTO;
import org.uw.nwgc.multiomics.core.sample.dto.SampleDTO;
import org.uw.nwgc.multiomics.core.tag.Tag;
import org.uw.nwgc.multiomics.core.tag.TagRepository;
import org.uw.nwgc.multiomics.core.tag.TagService;
import org.uw.nwgc.multiomics.core.tag.ontology.Tissue;
import org.uw.nwgc.multiomics.core.tag.ontology.TissueDTO;
import org.uw.nwgc.multiomics.core.user.CurrentUserResolver;
import org.uw.nwgc.multiomics.core.user.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class SampleService {

    private final SampleRepository sampleRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final SampleMapper sampleMapper;
    private final CurrentUserResolver currentUserResolver;

    public SampleService(
            SampleRepository sampleRepository,
            TagRepository tagRepository,
            TagService tagService,
            SampleMapper sampleMapper,
            CurrentUserResolver currentUserResolver
    ) {
        this.sampleRepository = sampleRepository;
        this.tagRepository = tagRepository;
        this.tagService = tagService;
        this.sampleMapper = sampleMapper;
        this.currentUserResolver = currentUserResolver;
    }

    public Optional<Sample> getById(UUID id) {
        return sampleRepository.findById(id);
    }

    public Optional<List<Sample>> findByIds(List<UUID> ids) {
        return sampleRepository.findByIdIn(ids);
    }

    public List<Sample> getAll() {
        return sampleRepository.findAll();
    }

    public List<SampleDTO> findSamplesByCriteria(List<UUID> projectIds, List<UUID> technologyIds) {
        Specification<Sample> spec = Specification.where(null);

        if (projectIds != null && !projectIds.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Object, Object> tagJoin = root.join("tags");
                return cb.and(
                        tagJoin.get("id").in(projectIds),
                        cb.equal(tagJoin.get("label"), "Project")
                );
            });
        }

        if (technologyIds != null && !technologyIds.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Object, Object> tagJoin = root.join("tags");
                return cb.and(
                        tagJoin.get("id").in(technologyIds),
                        cb.equal(tagJoin.get("label"), "Technology")
                );
            });
        }

        return sampleRepository.findAll(spec)
                .stream()
                .map(sampleMapper::toDTO)
                .toList();
    }


    public List<Sample> fuzzyFindByName(String name, Integer limit) {
        Pageable page = PageRequest.of(0, limit);

        return sampleRepository.findByNameContainingIgnoreCase(name);
    }

    public Optional<Sample> findExistingIntegrationSample(String externalLimsId, String integrationSource) {
        List<Sample> samples = sampleRepository.findByExternalLimsIdAndIntegrationSource(externalLimsId, integrationSource);
        if (samples.size() == 1) {
            return Optional.of(samples.getFirst());
        }

        return Optional.empty();
    }

    public Sample save(Sample sample) throws AuthException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = currentUserResolver.resolve((UserDetails) auth.getPrincipal());

        if (!sample.getUsers().isEmpty() && !user.inUserList(sample.getUsers())) {
            throw new AuthException("User not allowed to update Sample");
        }

        return sampleRepository.save(sample);
    }

    @Transactional
    public Sample save(SampleDTO sampleDTO) throws AuthException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = currentUserResolver.resolve((UserDetails) auth.getPrincipal());

        Sample sample = new Sample(user);
        if (sampleDTO.getId() != null) {
            sample = getById(sampleDTO.getId()).orElseThrow(() ->
                    new ObjectNotFoundException("Sample not found", sampleDTO.getId()));

            if (!sample.getUsers().isEmpty() && !user.inUserList(sample.getUsers())) {
                throw new AuthException("User not allowed to update Sample");
            }
        }

        List<Tissue> tissues = new ArrayList<>();
        if (sampleDTO.getOmicResources() != null) {
            for (var resource : sampleDTO.getOmicResources()) {
                TissueDTO tissueDTO = resource.getTissue();
                if (tissueDTO == null || tissueDTO.getUrl() == null) continue;
                String url = tissueDTO.getUrl();
                tagService.getTissueByUrl(url).ifPresent(tissues::add);
            }
        }

        sampleMapper.updateFromDto(sampleDTO, sample, tissues);
        sample.getUsers().add(user);

        sample.setModifier(user);
        sample.setModificationDateTime(LocalDateTime.now());

        return sampleRepository.save(sample);
    }

    public Sample deleteTag(SampleDTO sampleDTO, UUID tagId) throws AuthException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = currentUserResolver.resolve((UserDetails) auth.getPrincipal());

        Sample sample = new Sample(user);
        if (sampleDTO.getId() != null) {
            sample = getById(sampleDTO.getId()).orElseThrow(() -> new ObjectNotFoundException("Sample not found", sampleDTO.getId()));

            if (!sample.getUsers().isEmpty() && !user.inUserList(sample.getUsers())) {
                throw new AuthException("User not allowed to update Sample");
            }
        }

        Optional<Tag> optionalTag = sample.getTags().stream()
                .filter(tag -> tagId.equals(tag.getId()))
                .findFirst();
        if (optionalTag.isPresent()) {
            sample.getTags().remove(optionalTag.get());
            sample = sampleRepository.save(sample);
            tagRepository.delete(optionalTag.get());
        }

        return sample;
    }

    @Transactional
    public Sample createExternalSample(CreateSampleFromExternalDTO sampleDTO) throws AuthException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = currentUserResolver.resolve((UserDetails) auth.getPrincipal());

        Sample sample = new Sample(user);
        List<Sample> samples = sampleRepository.findByExternalLimsIdAndIntegrationSource
                (sampleDTO.getExternalLimsId(), sampleDTO.getIntegrationSource());
        if (samples.size() == 1) {
            sample = samples.getFirst();
        }

        if (!sample.getUsers().isEmpty() && !user.inUserList(sample.getUsers())) {
            throw new AuthException("User not allowed to update Sample");
        }

        sampleMapper.createFromExternalSample(sampleDTO, sample);
        sample.getUsers().add(user);

        // Get existing omic resources
        Map<String, OmicResource> resourcesMap = sample.getOmicResources()
                .stream()
                .collect(Collectors.toMap(OmicResource::getPath, Function.identity()));

        for (String fileName : sampleDTO.getFilePaths()) {
            if (resourcesMap.containsKey(fileName)) continue;

            OmicResource resource = new OmicResource(sample, fileName);
            resource.setSample(sample);
            sample.getOmicResources().add(resource);
        }

        return sampleRepository.save(sample);
    }

    public Sample addResourcesToExternalSample(AddExternalSampleResourcesDTO sampleDTO) throws AuthException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = currentUserResolver.resolve((UserDetails) auth.getPrincipal());

        if (sampleDTO.sampleId() == null) {
            throw new ObjectNotFoundException("Sample not found", sampleDTO.sampleId());
        }

        Sample sample;
        sample = getById(sampleDTO.sampleId())
                .orElseThrow(() -> new ObjectNotFoundException("Sample not found", sampleDTO.sampleId()));

        if (!sample.getUsers().isEmpty() && !user.inUserList(sample.getUsers())) {
            throw new AuthException("User not allowed to update Sample");
        }

        sample.setModifier(user);
        sample.setModificationDateTime(LocalDateTime.now());

        return sample;
    }

    @Transactional
    public Sample create(CreateSampleDTO sampleDTO) throws AuthException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = currentUserResolver.resolve((UserDetails) auth.getPrincipal());

        Sample sample = new Sample(user);
        sampleMapper.createFromExternalSample(sampleDTO, sample);
        sample.getUsers().add(user);

        return sampleRepository.save(sample);
    }
}
