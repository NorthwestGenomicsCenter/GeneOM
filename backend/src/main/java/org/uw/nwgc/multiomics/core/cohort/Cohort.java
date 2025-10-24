package org.uw.nwgc.multiomics.core.cohort;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.uw.nwgc.multiomics.core.resources.OmicResource;
import org.uw.nwgc.multiomics.core.sample.Sample;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cohort {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;
    private String name;
    private String description;

    @ManyToMany
    private Set<Sample> samples;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OmicResource> omicResources = new HashSet<>();


    public Cohort(CohortDTO cohortDto) {
        this.name = cohortDto.getName();
        this.description = cohortDto.getDescription();
    }
}
