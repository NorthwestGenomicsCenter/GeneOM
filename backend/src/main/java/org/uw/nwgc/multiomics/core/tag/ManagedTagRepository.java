package org.uw.nwgc.multiomics.core.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.uw.nwgc.multiomics.core.enums.ManageTagType;

public interface ManagedTagRepository extends JpaRepository<ManagedTag, UUID>, ManagedTagRepositoryCustom  {
    List<ManagedTag> findByType(ManageTagType type);
    ManagedTag findByTypeAndValue(ManageTagType type, String value);

    @Query("SELECT t.value FROM ManagedTag t WHERE t.type = :type")
    List<String> findAllValues(ManageTagType type);
    boolean existsByTypeAndValue(ManageTagType type, String value);
}