package org.uw.nwgc.multiomics.core.sample;

import jakarta.persistence.criteria.Join;
import jakarta.security.auth.message.AuthException;
import org.hibernate.ObjectNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.uw.nwgc.multiomics.core.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SampleService {

    private final SampleRepository sampleRepository;
    private final TagRepository tagRepository;

    public SampleService(SampleRepository sampleRepository, TagRepository tagRepository) {
        this.sampleRepository = sampleRepository;
        this.tagRepository = tagRepository;
    }

    public Optional<Sample> getById(UUID id) {
        return sampleRepository.findById(id);
    }

    public List<Sample> getAll() {
        return sampleRepository.findAll();
    }

    public List<SampleDTO> findSamplesByCriteria(List<UUID> projectIds, List<UUID> technologyIds) {
        Specification<Sample> spec = Specification.where(null);

        if (projectIds != null && !projectIds.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("project").get("id").in(projectIds));
        }

        if (technologyIds != null && !technologyIds.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Object, Object> techJoin = root.join("technologies");
                return techJoin.get("id").in(technologyIds);
            });
        }

        return sampleRepository.findAll(spec)
                .stream()
                .map(SampleDTO::new)
                .collect(Collectors.toList());
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

        sample.update(sampleDTO);
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
