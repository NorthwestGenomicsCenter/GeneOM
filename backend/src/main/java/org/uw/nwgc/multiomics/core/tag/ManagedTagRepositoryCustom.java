package org.uw.nwgc.multiomics.core.tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.uw.nwgc.multiomics.core.enums.ManageTagType;

public interface ManagedTagRepositoryCustom {
    Page<ManagedTagWithSampleCount> findWithSampleCountsByType(
            ManageTagType type,
            Specification<ManagedTag> spec,
            Pageable pageable
    );
}

