package org.uw.nwgc.multiomics.core.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.uw.nwgc.multiomics.core.analysis.Analysis;
import org.uw.nwgc.multiomics.core.snapshot.dto.SaveAnalysisPositionDTO;

import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "analysis_position_snapshot")
public class AnalysisPositionSnapshot {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    @JsonIgnore
    private Analysis analysis;

    private String chromosome;

    @Column(name = "start_position")
    private String start;
    @Column(name = "end_position")
    private String end;
    private String notes;

    public  AnalysisPositionSnapshot(SaveAnalysisPositionDTO dto, Analysis analysis) {
        this.analysis = analysis;
        this.chromosome = dto.chromosome();
        this.start = dto.start();
        this.end = dto.end();
        this.notes = dto.notes();
    }
}
