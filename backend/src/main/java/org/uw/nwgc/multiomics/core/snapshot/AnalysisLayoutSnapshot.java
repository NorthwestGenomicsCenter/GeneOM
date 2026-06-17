package org.uw.nwgc.multiomics.core.snapshot;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.springframework.context.annotation.Configuration;
import org.uw.nwgc.multiomics.core.analysis.Analysis;
import org.uw.nwgc.multiomics.core.snapshot.dto.SaveAnalysisLayoutDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "analysis_layout_snapshot")
public class AnalysisLayoutSnapshot {

    public AnalysisLayoutSnapshot(SaveAnalysisLayoutDTO dto, Analysis analysis) {
        this.analysis = analysis;
        this.layout = dto.getLayout();
        this.selectedResources = dto.getSelectedResources();
        this.created = LocalDateTime.now();
    }

    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    public UUID id;
    public LocalDateTime created;
    public String notes;

    public String layout;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    public Map<UUID, List<ComponentResources>> selectedResources;

    @ManyToOne
    public Analysis analysis;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentResources {
        public UUID id;
        public Object config;
    }
}
