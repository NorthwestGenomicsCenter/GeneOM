package org.uw.nwgc.multiomics.core.user;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

public enum AuthorityEnum implements GrantedAuthority {

    ROLE_ADMIN,
    ROLE_USER,
    ROLE_GUEST;

    @Override
    public String getAuthority() {
        return name();
    }
}
