package org.uw.nwgc.multiomics.core.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uw.nwgc.multiomics.core.user.dto.ChangePasswordRequestDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        User user = userDto.toUser(passwordEncoder, Set.of());
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

    @Transactional
    @PostMapping("/change-password")
    public ResponseEntity<?> changeUserPassword(@RequestBody ChangePasswordRequestDTO requestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        if (!user.getAuthorities().contains(AuthorityEnum.ROLE_ADMIN) && !user.getUsername().equals(requestDTO.username())) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (requestDTO.newPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        user.setPassword(passwordEncoder.encode(requestDTO.newPassword()));
        user.setFirstLogin(false);
        userService.saveUser(user);

        return ResponseEntity.ok("Successfully changed password");
    }

}
