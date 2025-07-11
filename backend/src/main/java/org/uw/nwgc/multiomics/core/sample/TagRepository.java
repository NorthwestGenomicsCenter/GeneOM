package org.uw.nwgc.multiomics.core.sample;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID>  {
    List<Tag> findByLabel(String label);
}