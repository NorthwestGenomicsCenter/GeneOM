package org.uw.nwgc.multiomics.core.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CreateUserDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<String> permissions;

    public User toUser(PasswordEncoder passwordEncoder, Set<AuthorityEnum> authorities) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);

        if (this.permissions != null) {
            user.setAuthorities(this.permissions
                    .stream()
                    .map(AuthorityEnum::valueOf)
                    .collect(Collectors.toSet()));
        } else {
            user.setAuthorities(authorities);
        }

        return user;
    }
}
