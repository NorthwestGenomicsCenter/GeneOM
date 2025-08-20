package org.uw.nwgc.multiomics.core.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/users/")
public class UserController {

    UserService userService;
    AuthorityRepo authorityRepo;
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(
            UserService userService,
            PasswordEncoder passwordEncoder,
            AuthorityRepo authorityRepo
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepo = authorityRepo;
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> loadAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream()
                .map(UserDTO::new)
                .toList();

        return ResponseEntity.ok(users);
    }

    @Transactional
    @PostMapping("/create")
    public ResponseEntity<UserDTO> createNewUser(@RequestBody CreateUserDTO userDto) {
        Optional<Authority> authority = authorityRepo.findById(1L);
        if (authority.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userDto.toUser(passwordEncoder, Set.of(authority.get()));
        userService.saveUser(user);

        return ResponseEntity.ok(new UserDTO(user));
    }

    @Transactional
    @PostMapping("/disable")
    public ResponseEntity<UserDTO> createNewUser(@PathVariable String userName) {
        User user = userService.loadUserByUsername(userName);
        user.isEnabled();
        userService.saveUser(user);

        return ResponseEntity.ok(new UserDTO(user));
    }

}
