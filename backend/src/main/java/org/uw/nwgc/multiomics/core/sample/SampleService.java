package org.uw.nwgc.multiomics.core.sample;

import jakarta.security.auth.message.AuthException;
import jakarta.transaction.Transactional;
import org.hibernate.ObjectNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.uw.nwgc.multiomics.core.tag.Tag;
import org.uw.nwgc.multiomics.core.tag.TagRepository;
import org.uw.nwgc.multiomics.core.tag.TagService;
import org.uw.nwgc.multiomics.core.tag.ontology.Tissue;
import org.uw.nwgc.multiomics.core.tag.ontology.TissueDTO;
import org.uw.nwgc.multiomics.core.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SampleService {

    private final SampleRepository sampleRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;

    public SampleService(
            SampleRepository sampleRepository,
            TagRepository tagRepository,
            TagService tagService) {
        this.sampleRepository = sampleRepository;
        this.tagRepository = tagRepository;
        this.tagService = tagService;
    }

    public Optional<Sample> getById(UUID id) {
        return sampleRepository.findById(id);
    }

    public List<Sample> getAll() {
        return sampleRepository.findAll();
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
        User user = (User) auth.getPrincipal();
        if (!sample.getUsers().isEmpty() && !user.inUserList(sample.getUsers())) {
            throw new AuthException("User not allowed to update Sample");
        }

        return sampleRepository.save(sample);
    }

    @Transactional
    public Sample save(SampleDTO sampleDTO) throws AuthException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Sample sample = new Sample();
        if (sampleDTO.getId() != null) {
            sample = getById(sampleDTO.getId()).orElseThrow(() -> new ObjectNotFoundException("Sample not found", sampleDTO.getId()));

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

        SampleMapper.updateFromDto(sampleDTO, sample, tissues);
        sample.getUsers().add(user);
        return sampleRepository.save(sample);
    }

    public Sample deleteTag(SampleDTO sampleDTO, UUID tagId) throws AuthException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Sample sample = new Sample();
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

}
