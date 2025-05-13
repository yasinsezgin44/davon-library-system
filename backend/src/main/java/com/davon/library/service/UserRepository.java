package com.davon.library.service;

import com.davon.library.model.User;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(Long id);
}