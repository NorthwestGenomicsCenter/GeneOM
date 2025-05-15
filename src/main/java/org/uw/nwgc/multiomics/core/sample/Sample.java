package org.uw.nwgc.multiomics.core.sample;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sample {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sample_seq")
    @SequenceGenerator(name = "sample_seq", sequenceName = "sample_seq", allocationSize = 1)
    private Integer id;
    private String name;
    private String externalLimsId;

    protected Sample update(SampleDTO dto) {
        this.name = dto.getName();
        this.externalLimsId = dto.getExternalLimsId();

        return this;
    }
}