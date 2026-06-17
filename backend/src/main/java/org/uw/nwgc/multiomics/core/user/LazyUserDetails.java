package org.uw.nwgc.multiomics.core.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class LazyUserDetails implements UserDetails {
    private final String username;
    private final UserService userService;
    private UserDetails delegate; // loaded on first access

    public LazyUserDetails(String username, UserService userService) {
        this.username = username;
        this.userService = userService;
    }

    public UserDetails delegate() {
        if (delegate == null) {
            delegate = userService.loadUserByUsername(username);
        }
        return delegate;
    }

    @Override public String getUsername() { return username; } // no DB hit
    @Override public String getPassword() { return delegate().getPassword(); } // DB hit
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return delegate().getAuthorities(); } // DB hit
    @Override public boolean isAccountNonExpired() { return delegate().isAccountNonExpired(); }
    @Override public boolean isAccountNonLocked() { return delegate().isAccountNonLocked(); }
    @Override public boolean isCredentialsNonExpired() { return delegate().isCredentialsNonExpired(); }
    @Override public boolean isEnabled() { return delegate().isEnabled(); }
}