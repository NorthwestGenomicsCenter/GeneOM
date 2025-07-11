package org.uw.nwgc.multiomics.core.tag;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Tag {

    public record SampleTagDTO(UUID id, String label, String value) {};

    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private String label;
    private String value;

    public void updateTag(SampleTagDTO sampleTagDTO) {
        this.label = sampleTagDTO.label();
        this.value = sampleTagDTO.value();
    }

}
