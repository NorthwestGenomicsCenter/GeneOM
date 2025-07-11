package org.uw.nwgc.multiomics.core.tag;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uw.nwgc.multiomics.core.dto.DataTable.DataTablePaginationEvent;
import org.uw.nwgc.multiomics.core.enums.ManageTagType;
import org.uw.nwgc.multiomics.core.tag.ontology.ImportHumanPhenotypeOntology;
import org.uw.nwgc.multiomics.core.tag.ontology.Tissue;
import org.uw.nwgc.multiomics.core.tag.ontology.TissueDTO;

import java.util.List;

@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/select/{type}")
    public ResponseEntity<?> getTagType(@PathVariable ManageTagType type) {
        return ResponseEntity.ok(type.name());
    }

    public record CreateTagDTO(String type, String text) {};

    @PostMapping("/save")
    public ResponseEntity<?> saveTagType(@RequestBody CreateTagDTO createTagDTO) {
        Tag tag = new Tag();
        tag.setLabel(createTagDTO.text);
        tag.setValue(createTagDTO.text);
        tagService.addTag(tag);

        return ResponseEntity.ok(tag);
    }

    @GetMapping("/managed")
    public ResponseEntity<ManageTagType[]> getManagedTypes() {
        return ResponseEntity.ok(ManageTagType.values());
    }

    @PostMapping("/managed/save")
    public ResponseEntity<?> saveManagedTag(@RequestBody CreateTagDTO createTagDTO) {
        ManagedTag tag = new ManagedTag();
        tag.setType(ManageTagType.TECHNOLOGY);
        tag.setValue(createTagDTO.text);
        tagService.addManagedTagValue(tag);

        return ResponseEntity.ok(tag);
    }

    @Transactional
    @GetMapping("/import/ontology")
    public ResponseEntity<?> importOntology() {
        List<Tissue> tissues = ImportHumanPhenotypeOntology.importData();
        tagService.saveTissues(tissues);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/ontology/featured")
    public ResponseEntity<List<TissueDTO>> featuredOntology() {
        var tissues = tagService.getFeaturedTissues();
        List<TissueDTO> dto = tissues.stream()
                .map(TissueDTO::new)
                .toList();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/ontology/all")
    public ResponseEntity<List<TissueDTO>> allOntology() {
        var tissues = tagService.getAllTissues();
        List<TissueDTO> dto = tissues.stream()
                .map(TissueDTO::new)
                .toList();

        return ResponseEntity.ok(dto);
    }

    public record TissueTableDTO(List<TissueDTO> tissues, Long records) {}
    @PostMapping("/ontology/pagination")
    public ResponseEntity<TissueTableDTO> pageOntology(@RequestBody(required = false) DataTablePaginationEvent event) {
        int pageNumber = event.getPage();
        int pageSize = event.getRows();

        Page<Tissue> tissues;
        if (event.getFilters().getName().getValue() == null) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("name").ascending());
            tissues = tagService.getTissuesPageable(pageable);
        } else {
            String name = event.getFilters().getName().getValue().toString();
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            tissues = tagService.getTissuesByNamePageable(name, pageable);
        }

        List<TissueDTO> dto = tissues.stream()
                .map(TissueDTO::new)
                .toList();

        return ResponseEntity.ok(new TissueTableDTO(dto, tissues.getTotalElements()));
    }

    @GetMapping("/ontology/search/{name}")
    public ResponseEntity<List<TissueDTO>> searchOntology(@PathVariable String name) {
        return ResponseEntity.ok(tagService.getTissuesLike(name));
    }

    @PostMapping("/ontology/toggle-featured")
    public ResponseEntity<TissueDTO> toggleFeaturedOntology(@RequestBody TissueDTO dto) {
        var tissue = tagService.toggleFeatured(dto);
        if (tissue == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new TissueDTO(tissue));
    }

}
