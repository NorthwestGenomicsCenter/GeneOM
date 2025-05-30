package org.uw.nwgc.multiomics.core.genes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GeneRepository extends JpaRepository<Gene, Integer> {

    @Query("select g from Gene g where g.symbol ilike %:name% order by g.symbol limit 5")
    List<Gene> fuzzyFindByName(@Param("name") String name);

    @Query(value = """
    select g.*
    from public.gene g
      join public.gene_groups gg on g.id = gg.gene_id
    where gg.gene_group in (
      select gg.gene_group
      from public.gene g
        join public.gene_groups gg on g.id = gg.gene_id
      where g.symbol = :name )
    """,
    nativeQuery=true)
    List<Gene> findRelatedGenes(@Param("name") String name);
}
