package org.uw.nwgc.multiomics.core.auth;

import lombok.Data;

@Data
public class SignInRequest {
    private String username;
    private String password;
}
