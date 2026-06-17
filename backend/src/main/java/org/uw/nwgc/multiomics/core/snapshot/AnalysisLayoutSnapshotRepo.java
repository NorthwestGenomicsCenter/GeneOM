package org.uw.nwgc.multiomics.core.snapshot;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AnalysisLayoutSnapshotRepo extends JpaRepository<AnalysisLayoutSnapshot, UUID> {
    Optional<AnalysisLayoutSnapshot> findTopByAnalysisIdOrderByCreatedDesc(UUID analysisId);
}
