package com.davon.library.service;

import com.davon.library.model.User;
import com.davon.library.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    @Inject
    UserRepository userRepository;

    @Inject
    Pbkdf2PasswordHash passwordHash;

    public User authenticate(String username, String password) {
        log.info("Authenticating user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotAuthorizedException("Invalid credentials"));

        if (!passwordHash.verify(password.toCharArray(), user.getPasswordHash())) {
            throw new NotAuthorizedException("Invalid credentials");
        }

        if (!user.getActive()) {
            throw new NotAuthorizedException("User account is not active");
        }

        return user;
    }

    @Transactional
    public User register(User user, String password) {
        log.info("Registering new user: {}", user.getUsername());
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        user.setPasswordHash(passwordHash.generate(password.toCharArray()));
        userRepository.persist(user);
        return user;
    }
}
