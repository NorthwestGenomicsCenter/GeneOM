package org.uw.nwgc.multiomics.core.sample;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.uw.nwgc.multiomics.core.analysis.Analysis;
import org.uw.nwgc.multiomics.core.resources.OmicResource;
import org.uw.nwgc.multiomics.core.tag.ManagedTag;
import org.uw.nwgc.multiomics.core.tag.Tag;
import org.uw.nwgc.multiomics.core.user.User;
import org.uw.nwgc.multiomics.util.FileUtil;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @ManyToOne
    private User creator;
    private LocalDateTime creationDateTime;

    @ManyToOne
    private User modifier;
    private LocalDateTime modificationDateTime;

    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OmicResource> omicResources = new HashSet<>();

    @ManyToMany
    private Set<User> users = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<ManagedTag> managedTags = new HashSet<>();

    @JsonBackReference
    @ManyToMany(mappedBy = "samples")
    private Set<Analysis> analysis = new HashSet<>();

    // -- CONSTRUCTORS --------------------------------------------------------

    public Sample(User user) {
        this.creator = user;
        this.creationDateTime = LocalDateTime.now();
    }

    // -- METHODS -------------------------------------------------------------

    public Map<String, OmicResource> resourceFilenameMap() {
        return omicResources
            .stream()
            .collect(Collectors.toMap(
                    r -> FileUtil.resolvePath(r.getPath()).getFileName().toString(),
                    Function.identity()
            ));
    }

}