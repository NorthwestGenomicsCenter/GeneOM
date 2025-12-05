package org.uw.nwgc.multiomics.core.snapshot;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uw.nwgc.multiomics.core.analysis.Analysis;
import org.uw.nwgc.multiomics.core.analysis.AnalysisService;
import org.uw.nwgc.multiomics.core.snapshot.dto.SaveAnalysisLayoutDTO;
import org.uw.nwgc.multiomics.core.snapshot.dto.SaveAnalysisPositionDTO;

import java.util.Optional;
import java.util.UUID;

@Transactional
@RestController
@RequestMapping("/snapshot")
public class SnapshotController {

    private final AnalysisService analysisService;
    private final AnalysisLayoutSnapshotRepo analysisLayoutSnapshotRepo;
    private final AnalysisPositionSnapshotRepo analysisPositionSnapshotRepo;

    public SnapshotController(
            AnalysisService analysisService,
            AnalysisLayoutSnapshotRepo analysisLayoutSnapshotRepo,
            AnalysisPositionSnapshotRepo analysisPositionSnapshotRepo
    ){
        this.analysisLayoutSnapshotRepo = analysisLayoutSnapshotRepo;
        this.analysisService = analysisService;
        this.analysisPositionSnapshotRepo = analysisPositionSnapshotRepo;
    }

    @GetMapping("/analysis-layout/{analysisId}")
    public ResponseEntity<?> getAnalysisLayout(@PathVariable UUID analysisId) {
        Optional<Analysis> analysisQuery = analysisService.findById(analysisId);
        if (analysisQuery.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Optional<AnalysisLayoutSnapshot> snapshot = analysisLayoutSnapshotRepo.findTopByAnalysisIdOrderByCreatedDesc(analysisId);
        if (snapshot.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(snapshot.get());
    }

    @PostMapping("/analysis-layout/{analysisId}")
    public ResponseEntity<?> setAnalysisLayout(@PathVariable UUID analysisId, @RequestBody SaveAnalysisLayoutDTO dto) {
        Optional<Analysis> analysisQuery = analysisService.findById(analysisId);
        if (analysisQuery.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        AnalysisLayoutSnapshot snapshot = new AnalysisLayoutSnapshot(dto, analysisQuery.get());
        analysisLayoutSnapshotRepo.save(snapshot);
        return ResponseEntity.ok(analysisId);
    }

    @PostMapping("/analysis-position/{analysisId}")
    public ResponseEntity<?> saveAnalysisPosition(@PathVariable UUID analysisId, @RequestBody SaveAnalysisPositionDTO dto) {
        Optional<Analysis> analysisQuery = analysisService.findById(analysisId);
        if (analysisQuery.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        AnalysisPositionSnapshot  snapshot = new AnalysisPositionSnapshot(dto, analysisQuery.get());
        analysisPositionSnapshotRepo.save(snapshot);
        return ResponseEntity.ok(analysisId);
    }
}
