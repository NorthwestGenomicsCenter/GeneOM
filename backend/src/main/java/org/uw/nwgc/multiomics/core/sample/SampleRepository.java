package org.uw.nwgc.multiomics.core.sample;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SampleRepository extends JpaRepository<Sample, UUID>, QueryByExampleExecutor<Sample>, JpaSpecificationExecutor<Sample> {
    List<Sample> findByNameContainingIgnoreCase(String namePart);
    Page<Sample> findByNameContainingIgnoreCase(String namePart, Pageable pg);
    List<Sample> findByExternalLimsIdAndIntegrationSource(String externalLimsId, String integrationSource);
    List<Sample> findByTags_Id(UUID tagsId);
    long countByTags_Id(UUID tagId);
}
