package org.uw.nwgc.multiomics.core.analysis.dto;

import lombok.Data;
import org.uw.nwgc.multiomics.core.user.UserDTO;

import java.util.Set;
import java.util.UUID;

@Data
public class AddAnalysisUsersDTO {
    UUID analysisId;
    Set<UserDTO> users;
}
