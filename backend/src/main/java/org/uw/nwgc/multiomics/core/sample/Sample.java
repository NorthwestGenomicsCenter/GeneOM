package org.uw.nwgc.multiomics.core.sample;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.uw.nwgc.multiomics.core.resources.OmicResource;
import org.uw.nwgc.multiomics.core.tag.ManagedTag;
import org.uw.nwgc.multiomics.core.tag.Tag;
import org.uw.nwgc.multiomics.core.user.User;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sample {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;
    private String name;
    private String externalLimsId;
    private String integrationSource;
    private String description;
    private String externalProjectName;

    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OmicResource> omicResources = new HashSet<>();

    @ManyToMany
    private Set<User> users = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<ManagedTag> managedTags = new HashSet<>();

}