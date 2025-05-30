package org.uw.nwgc.multiomics.core.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uw.nwgc.multiomics.assay.isoform.Isoform;
import org.uw.nwgc.multiomics.assay.isoform.IsoformRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OmicService {

    private final OmicRepo omicRepo;

    public OmicResource findById(int id) {
        return omicRepo.findById(id).orElse(null);
    }

    public OmicResource save(OmicResource resource){
        return omicRepo.save(resource);
    }
}
