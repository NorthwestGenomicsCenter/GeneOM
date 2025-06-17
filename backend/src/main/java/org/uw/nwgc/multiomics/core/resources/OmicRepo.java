package org.uw.nwgc.multiomics.core.resources;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OmicRepo extends JpaRepository<OmicResource, UUID> {
    List<OmicResource> findByIdIn(List<UUID> omicId);
}