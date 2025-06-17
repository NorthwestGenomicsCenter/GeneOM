package org.uw.nwgc.multiomics.core.resources;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.uw.nwgc.multiomics.core.sample.Sample;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "omic_resouce")
public class OmicResource {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sample_id")
    @JsonBackReference
    private Sample sample;

    private String path;

    private Boolean igvTrack;

    private Class<? extends VizTransformer> vizTransformer;

    public OmicResource(OmicResourceDTO dto, Sample sample) {
        this.path = dto.getPath();
        this.sample = sample;
        this.vizTransformer = DiffExpressionVisualizer.class;
    }

    public List<String> getVisualizers() {
        try {
            VizTransformer transformer = vizTransformer.getDeclaredConstructor()
                    .newInstance();
            return transformer.visualizationComponents();

        } catch (Exception ignored) {}

        return null;
    }

    public void update(OmicResourceDTO omicResourceDTO) {
        this.path = omicResourceDTO.getPath();
        this.igvTrack = omicResourceDTO.getIgvTrack();
    }
}
