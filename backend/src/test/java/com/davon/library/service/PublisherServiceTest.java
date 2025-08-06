package com.davon.library.service;

import com.davon.library.model.Publisher;
import com.davon.library.repository.PublisherRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class PublisherServiceTest {

    @Inject
    PublisherService publisherService;

    @Inject
    PublisherRepository publisherRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        publisherRepository.deleteAll();
    }

    @Test
    @Transactional
    void testCreatePublisher() {
        Publisher publisher = new Publisher();
        publisher.setName("Test Publisher");
        publisherService.createPublisher(publisher);
        assertNotNull(publisher.getId());
        assertEquals(1, publisherRepository.count());
    }

    @Test
    @Transactional
    void testGetPublisherById() {
        Publisher publisher = new Publisher();
        publisher.setName("Test Publisher");
        publisherRepository.persist(publisher);

        Publisher found = publisherService.getPublisherById(publisher.getId());
        assertNotNull(found);
        assertEquals("Test Publisher", found.getName());
    }
}
