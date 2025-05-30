package org.uw.nwgc.multiomics.core.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";
    private Integer id;
    private String username;
    private List<String> roles;
}