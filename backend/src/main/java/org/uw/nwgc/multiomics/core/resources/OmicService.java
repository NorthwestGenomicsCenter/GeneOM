package org.uw.nwgc.multiomics.core.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OmicService {

    private final OmicRepo omicRepo;

    public OmicResource findById(UUID id) {
        return omicRepo.findById(id).orElse(null);
    }

    public OmicResource save(OmicResource resource){
        return omicRepo.save(resource);
    }

    public void delete(OmicResource resource) {
        omicRepo.delete(resource);
    }
}
