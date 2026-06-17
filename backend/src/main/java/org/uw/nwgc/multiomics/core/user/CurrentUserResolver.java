package org.uw.nwgc.multiomics.core.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserResolver {
    private final UserService userService;

    public CurrentUserResolver(UserService userService) {
        this.userService = userService;
    }

    public User resolve(UserDetails principal) {
        if (principal instanceof LazyUserDetails lazy) {
            return (User) lazy.delegate();
        }
        if (principal instanceof User user) {
            return user;
        }
        return (User) userService.loadUserByUsername(principal.getUsername());
    }
}