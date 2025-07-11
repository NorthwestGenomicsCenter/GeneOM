package org.uw.nwgc.multiomics.core.sample;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface SampleRepository extends JpaRepository<Sample, UUID>, JpaSpecificationExecutor<Sample> {

    List<Sample> findByNameContainingIgnoreCase(String namePart);
    Page<Sample> findByNameContainingIgnoreCase(String namePart, Pageable pg);
    List<Sample> findByExternalLimsIdAndIntegrationSource(String externalLimsId, String integrationSource);
}
