package com.davon.library.repository;

import com.davon.library.model.Member;
import com.davon.library.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {
    private InMemoryUserRepository repository;
    private Member member;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        member = new Member();
        member.setId(null);
        member.setUsername("testuser");
    }

    @Test
    void testSaveAndFindById() {
        User saved = repository.save(member);
        assertNotNull(saved.getId());
        Optional<User> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved, found.get());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<User> found = repository.findById(999L);
        assertTrue(found.isEmpty());
    }
}