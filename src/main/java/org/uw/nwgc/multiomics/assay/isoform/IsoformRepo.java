package org.uw.nwgc.multiomics.assay.isoform;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IsoformRepo extends JpaRepository<Isoform, Integer> {

    @Query("""
    select i from Isoform i where i.rankTop999Percentile is not null
    """)
    List<Isoform> findByCutoff();
}