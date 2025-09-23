package org.uw.nwgc.multiomics.core.analysis;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.uw.nwgc.multiomics.core.cohort.Cohort;
import org.uw.nwgc.multiomics.core.sample.Sample;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Analysis {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private String name;
    private String description;
    private String layout;
    private String selectedResources;

    @Enumerated(EnumType.STRING)
    private ReferenceGenome referenceGenome = ReferenceGenome.hg38; // default to hg38 for new analysis

    @ManyToOne(cascade = CascadeType.ALL)
    private CustomReference customReference;

    @ManyToMany
    private Set<Sample> samples;

    @ManyToMany
    private Set<Cohort> cohorts;

    private LocalDateTime lastModified;

}