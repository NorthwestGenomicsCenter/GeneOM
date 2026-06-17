package org.uw.nwgc.multiomics.core.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uw.nwgc.multiomics.core.user.CurrentUserResolver;
import org.uw.nwgc.multiomics.core.user.User;
import org.uw.nwgc.multiomics.core.user.UserRepo;
import org.uw.nwgc.multiomics.core.user.UserService;
import org.uw.nwgc.multiomics.util.JwtUtil;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final CurrentUserResolver currentUserResolver;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepository;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            CurrentUserResolver currentUserResolver,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,
            UserRepo userRepository,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.currentUserResolver = currentUserResolver;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> signin(@RequestBody SignInRequest signInRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtil.generateJwtToken(authentication, JwtUtil.ACCESS_EXPIRATION_SECONDS);
        String refreshToken = jwtUtil.generateJwtToken(authentication, JwtUtil.REFRESH_EXPIRATION_SECONDS);

        User userDetails = (User) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        LoginResponseDTO res = new LoginResponseDTO();
        res.setAccessToken(accessToken);
        res.setRefreshToken(refreshToken);
        res.setId(userDetails.getId());
        res.setUsername(userDetails.getUsername());
        res.setRoles(roles);

        return ResponseEntity.ok()
                .headers(jwtUtil.tokenHeaders(authentication))
                .body(res);
    }

    public record PasswordChangeRequest(String username, String password) {}

    @PostMapping("/change/password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.badRequest()
                    .body("Authentication required");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = currentUserResolver.resolve((UserDetails) auth.getPrincipal());

        user.setPassword(passwordEncoder.encode(passwordChangeRequest.password));
        userRepository.save(user);
        return ResponseEntity.ok("Password changed");
    }

    public record CreateUserDto(
            String username,
            String firstName,
            String lastName,
            String password) {}

    @PostMapping("/create/admin")
    public ResponseEntity<User> createAdminUser(@RequestBody CreateUserDto createUserDto) {
        User user = new User();
        user.setFirstName(createUserDto.firstName);
        user.setLastName(createUserDto.lastName);
        user.setPassword(passwordEncoder.encode(createUserDto.password));
        userService.saveUser(user);

        return ResponseEntity.ok(user);
    }

}
