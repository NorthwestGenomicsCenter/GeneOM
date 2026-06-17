package org.uw.nwgc.multiomics.core.resources;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.uw.nwgc.multiomics.assay.igv.Igv;
import org.uw.nwgc.multiomics.core.resources.dto.AddOmicsResourceDTO;
import org.uw.nwgc.multiomics.core.resources.dto.OmicResourceDTO;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.tag.ontology.Tissue;
import org.uw.nwgc.multiomics.service.assay_detector.types.DetectedFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "omic_resource")
public class OmicResource {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sample_id", nullable = true)
    @JsonBackReference
    private Sample sample;
    private String path;
    private String displayName;
    private String description;
    private Boolean igvTrack;

    @ManyToOne
    @JoinColumn(name = "tissue_type_url")
    private Tissue tissueType;

    private boolean uploaded;

    private List<Class<? extends VizTransformer>> vizTransformer = new ArrayList<>();

//    @Enumerated(EnumType.STRING)
//    private List<Transformer> transformers = new ArrayList<>();

    public OmicResource(Sample sample, String path) {
        this.path = path;
        this.sample = sample;
        if (!path.endsWith(".fastq.gz")) {
            this.vizTransformer = List.of(Igv.class);
        }
    }

    public OmicResource(AddOmicsResourceDTO dto, Sample sample) {
        this.path = dto.getPath();
        this.sample = sample;
        this.vizTransformer = List.of(Igv.class);
    }

    public OmicResource updateFromDetectedFile(DetectedFile detectedFile) {
        this.displayName = detectedFile.pattern().description();
        this.description = detectedFile.pattern().description();
        this.vizTransformer = detectedFile.pattern().resourceType().getTransformers();
        return this;
    }

    public List<String> getVisualizers() {
        try {
            return vizTransformer.stream()
                .map(c -> {
                    try {
                        var t = c.getDeclaredConstructor().newInstance();
                        return t.visualizationComponents();
                    } catch (Exception ignored) {}
                    return new ArrayList<String>();
                })
                .flatMap(List::stream)
                .toList();

        } catch (Exception ignored) {}

        return null;
    }

    public void update(OmicResourceDTO omicResourceDTO, List<Tissue> tissues) {
        this.path = omicResourceDTO.getPath();
        this.igvTrack = omicResourceDTO.getIgvTrack();
        for (Tissue tissue : tissues) {
            if (tissue != null && omicResourceDTO.getTissue() != null && omicResourceDTO.getTissue().getUrl().equals(tissue.getUrl())) {
                this.tissueType = tissue;
            }
        }
    }

}
