package org.uw.nwgc.multiomics.core.user;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository is an interface that provides access to data in a database
 */
public interface UserRepo extends JpaRepository<User, Integer> {

    @Transactional
    Optional<User> findByUsername(String username);
}
