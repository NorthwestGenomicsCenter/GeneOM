package org.uw.nwgc.multiomics.core.tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.uw.nwgc.multiomics.core.tag.ontology.Tissue;
import org.uw.nwgc.multiomics.core.tag.ontology.TissueDTO;
import org.uw.nwgc.multiomics.core.tag.ontology.TissueRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    private final TagRepository tagRepository;
    private final TissueRepository tissueRepository;
    private final ManagedTagRepository managedTagRepository;

    public TagService(
            TagRepository tagRepository,
            ManagedTagRepository managedTagRepository,
            TissueRepository tissueRepository
    ) {
        this.tagRepository = tagRepository;
        this.managedTagRepository = managedTagRepository;
        this.tissueRepository = tissueRepository;
    }

    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    public List<Tag> getTagsByType(String type) {
        // TODO: Fix implementation
        return tagRepository.findAll();
    }

    public Tag addTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public ManagedTag addManagedTagValue(ManagedTag tag) {
        return managedTagRepository.save(tag);
    }

    // Tissues

    public Optional<Tissue> getTissueByUrl(String url) {
        return tissueRepository.findById(url);
    }

    @Transactional
    public void saveTissues(List<Tissue> tissues) {
        tissueRepository.saveAll(tissues);
    }

    public List<Tissue> getFeaturedTissues() {
        return tissueRepository.findByFeatured(true);
    }

    public List<Tissue> getAllTissues() {
        return tissueRepository.findAll();
    }

    public Page<Tissue> getTissuesPageable(Pageable pageable) {
        return tissueRepository.findAll(pageable);
    }

    public Page<Tissue> getTissuesByNamePageable(String name, Pageable pageable) {
        return tissueRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Tissue toggleFeatured(TissueDTO dto) {
        Optional<Tissue> tissue = tissueRepository.findById(dto.getUrl());
        if (tissue.isPresent()) {
            tissue.get().setFeatured(dto.isFeatured());
            return tissueRepository.save(tissue.get());
        }

        return null;
    }

    public List<TissueDTO> getTissuesLike(String name) {
        return tissueRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(TissueDTO::new)
                .toList();
    }

    public long tissuesCount() {
        return tissueRepository.count();
    }
}
