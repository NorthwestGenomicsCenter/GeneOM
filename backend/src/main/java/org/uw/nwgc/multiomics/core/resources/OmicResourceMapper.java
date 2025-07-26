package org.uw.nwgc.multiomics.core.resources;

import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.sample.SampleDTO;
import org.uw.nwgc.multiomics.core.tag.ontology.Tissue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OmicResourceMapper {

    private final Map<String, VizTransformer> transformers;

    public OmicResourceMapper(List<VizTransformer> transformers) {
        this.transformers = transformers.stream()
                .collect(Collectors.toMap(t -> t.getClass().getSimpleName(), Function.identity()));
    }

    public OmicResource updateFromDTO(OmicResourceDTO dto, OmicResource resource, List<Tissue> tissues) {

        resource.setPath(dto.getPath());
        resource.setIgvTrack(dto.getIgvTrack());

    List<Class<? extends VizTransformer>> transformerList = new ArrayList<>();
        for (var visualizer : dto.getVisualizers()) {
            VizTransformer transformer = transformers.get(visualizer);
            if (transformer != null) {
                transformerList.add(transformer.getClass());
            }
        }

        resource.setVizTransformer(transformerList);
//
//        this.vizTransformer = omicResourceDTO.getVisualizers();
//        for (Tissue tissue : tissues) {
//            if (tissue != null && omicResourceDTO.getTissue() != null && omicResourceDTO.getTissue().getUrl().equals(tissue.getUrl())) {
//                this.tissueType = tissue;
//            }
//        }

//        resource.update(dto, tissues);

        return resource;
    }

    public static Collection<OmicResource> updateFromDTO(SampleDTO dto, Sample sample, List<Tissue> tissues) {
        Map<UUID, OmicResource> existingOmicResources = sample.getOmicResources()
                .stream()
                .collect(Collectors.toMap(OmicResource::getId, Function.identity()));

        if (dto.getOmicResources() != null && !dto.getOmicResources().isEmpty()) {
            for (OmicResourceDTO omicResourceDTO : dto.getOmicResources()) {
                OmicResource omicResource;
                if (omicResourceDTO.getOmicResourceId() == null) {
                    omicResource = new OmicResource();
                } else {
                    omicResource = existingOmicResources.computeIfAbsent(omicResourceDTO.getOmicResourceId(), k -> new OmicResource());
                }

                omicResource.update(omicResourceDTO, tissues);
                sample.getOmicResources().add(omicResource);
            }
        }

        return existingOmicResources.values();
    }
}
