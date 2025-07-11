package org.uw.nwgc.multiomics.core.tag.ontology;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TissueRepository extends JpaRepository<Tissue, String> {
    List<Tissue> findByFeatured(boolean featured);
    List<Tissue> findByNameContainingIgnoreCase(String name);

    @Query(value = """
    SELECT t FROM Tissue t
    WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))
    ORDER BY
        CASE WHEN LOWER(t.name) = LOWER( :name ) THEN 0 ELSE 1 END,
        t.name ASC
    """
    )
    Page<Tissue> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
}
