package org.uw.nwgc.multiomics.core.snapshot;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AnalysisPositionSnapshotRepo extends JpaRepository<AnalysisPositionSnapshot, UUID> {

}
