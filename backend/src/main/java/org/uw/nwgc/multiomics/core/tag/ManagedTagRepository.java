package org.uw.nwgc.multiomics.core.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManagedTagRepository extends JpaRepository<ManagedTag, UUID>  {}