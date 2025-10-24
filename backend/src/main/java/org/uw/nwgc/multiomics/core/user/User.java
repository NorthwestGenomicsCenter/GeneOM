package org.uw.nwgc.multiomics.core.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String firstName;
    private String lastName;
    private String password;
    private Boolean enabled;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "user_authorities",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "authority_id")
//    )
//    @Cascade(CascadeType.ALL)
//    @Fetch(FetchMode.SELECT)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_authorities",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "authority")
    private Set<AuthorityEnum> authorities = new HashSet<>();

    public User(
            String username,
            String password,
            String email,
            String firstname,
            String lastname,
            PasswordEncoder passwordEncoder
    ) {
                this.username = username;
        this.password = passwordEncoder.encode(password);
        this.email = email;
        this.enabled = true;
        this.firstName = firstname;
        this.lastName = lastname;
    }

    @Override
    public Set<AuthorityEnum> getAuthorities() {
        return authorities;
    }

    public boolean inUserList(Set<User> users) {
        return users.stream().anyMatch(u -> u.getId().equals(id));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
