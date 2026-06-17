package org.uw.nwgc.multiomics.core.analysis;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnalysisRepository extends JpaRepository<Analysis, UUID> {

    List<Analysis> findTop5ByOrderByLastModifiedDesc();
    List<Analysis> findByUsersIdOrderByLastModifiedDesc(Integer id);
    List<Analysis> findByUsersUsernameOrderByLastModifiedDesc(String username);
}