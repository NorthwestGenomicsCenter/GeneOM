package org.uw.nwgc.multiomics.core.resources;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.uw.nwgc.multiomics.assay.isoform.Isoform;

import java.util.List;

public interface OmicRepo extends JpaRepository<OmicResource, Integer> {
}