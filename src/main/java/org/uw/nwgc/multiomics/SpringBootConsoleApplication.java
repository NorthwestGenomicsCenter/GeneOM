package org.uw.nwgc.multiomics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.uw.nwgc.multiomics.core.user.Authority;
import org.uw.nwgc.multiomics.core.user.User;
import org.uw.nwgc.multiomics.core.user.UserService;

import java.util.HashSet;
import java.util.Set;

@Component
@ConditionalOnNotWebApplication
public class SpringBootConsoleApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(SpringBootConsoleApplication.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SpringBootConsoleApplication(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(SpringBootConsoleApplication.class, args);
        LOG.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        LOG.info("EXECUTING : command line runner");

        if (args.length == 0) {
            throw new IllegalArgumentException("No command line arguments provided");
        }

        String adminCommand = args[0];
        switch (adminCommand) {
            case "help" -> {
                LOG.info("HELP");
            }
            case "create-user" -> createUser(args);
            default -> LOG.error("INVALID COMMAND, try `gradlew admin ");
        }

    }

    private void createUser(String... args) {
        if (args.length != 6) {
            LOG.error("INVALID ARGUMENTS TO CREATE USER");
            LOG.error("USAGE : create-user <username> <password> <email> <firstname> <lastname>");
            return;
        }

        // Create the user
        String username = args[1];
        String password = passwordEncoder.encode(args[2]);
        String email = args[3];
        String firstname = args[4];
        String lastname = args[5];
        User user = new User(username, password, email, firstname, lastname);

        Set<Authority> authorities = new HashSet<>();
        authorities.add(new Authority("ROLE_USER"));
        authorities.add(new Authority("ROLE_ADMIN"));
        user.setAuthorities(authorities);

        userService.saveUser(user);
    }
}