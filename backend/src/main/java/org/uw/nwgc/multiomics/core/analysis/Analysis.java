package org.uw.nwgc.multiomics.core.analysis;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.uw.nwgc.multiomics.core.cohort.Cohort;
import org.uw.nwgc.multiomics.core.sample.Sample;
import org.uw.nwgc.multiomics.core.snapshot.AnalysisPositionSnapshot;
import org.uw.nwgc.multiomics.core.user.User;

import java.time.LocalDateTime;
import java.util.List;
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

    @ManyToMany
    private Set<User> users;

    @OneToMany(
            mappedBy = "analysis",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<AnalysisPositionSnapshot> savedPositions;

    private LocalDateTime lastModified;

}