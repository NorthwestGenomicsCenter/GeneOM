package org.uw.nwgc.multiomics.core.sample;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.uw.nwgc.multiomics.core.user.User;

import java.util.List;
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
    private String description;
    private String externalProjectName;

    @ManyToMany
    private List<User> users;

    protected Sample update(SampleDTO dto) {
        this.name = dto.getName();
        this.externalLimsId = dto.getExternalLimsId();
        this.description = dto.getDescription();
        this.externalProjectName = dto.getExternalProjectName();

        return this;
    }
}