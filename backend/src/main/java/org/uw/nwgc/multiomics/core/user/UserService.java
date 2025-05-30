package org.uw.nwgc.multiomics.core.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;

    public List<User> getAllUsers(){
        return userRepo.findAll();
    }

    public User saveUser(User user){
        return userRepo.save(user);
    }

    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username" + username));
    }

//    public User loadUserByUsername(String username) throws UsernameNotFoundException {
//        var password = new BCryptPasswordEncoder().encode("12345");
//        return new User(1, "mr83", "Matt", "Richardson", password, List.of());
//    }

}
