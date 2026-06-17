package org.uw.nwgc.multiomics.core.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

public class JwtUserDetails implements UserDetails {
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUserDetails(String username, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = authorities;
    }

    public boolean isAdmin() {
        if (Objects.isNull(authorities) || authorities.isEmpty()) {
            return false;
        }
        return authorities.contains(AuthorityEnum.ROLE_ADMIN);
    }

    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return null; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}