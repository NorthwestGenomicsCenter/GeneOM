package org.uw.nwgc.multiomics.core.cohort;

import org.springframework.data.jpa.repository.JpaRepository;
import org.uw.nwgc.multiomics.core.sample.Sample;

import java.util.UUID;

public interface CohortRepository extends JpaRepository<Cohort, UUID> {
}
