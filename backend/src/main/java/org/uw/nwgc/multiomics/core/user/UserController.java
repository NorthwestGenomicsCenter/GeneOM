package org.uw.nwgc.multiomics.core.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.uw.nwgc.multiomics.core.user.dto.ChangePasswordRequestDTO;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users/")
public class UserController {

    private final CurrentUserResolver currentUserResolver;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public UserController(
            CurrentUserResolver currentUserResolver,
            PasswordEncoder passwordEncoder,
            UserService userService
    ) {
        this.currentUserResolver = currentUserResolver;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = currentUserResolver.resolve((UserDetails) auth.getPrincipal());

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
