package org.uw.nwgc.multiomics.core.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users/")
public class UserController {

    UserService userService;
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(
            UserService userService,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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
        User user = userDto.toUser(passwordEncoder);
        userService.saveUser(user);

        return ResponseEntity.ok(new UserDTO(user));
    }

}
