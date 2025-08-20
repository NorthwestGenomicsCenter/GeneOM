package org.uw.nwgc.multiomics.core.tag;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.uw.nwgc.multiomics.core.enums.ManageTagType;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "managed_tag")
public class ManagedTag {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ManageTagType type;
    private String value;
    private String description;

    public ManagedTag() {}
    public ManagedTag(ManageTagType type, String value, String description) {
        this.type = type;
        this.value = value;
        this.description = description;
    }
}
