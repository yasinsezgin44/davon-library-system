package com.davon.library.service;

import com.davon.library.model.Publisher;
import com.davon.library.repository.PublisherRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class PublisherService {

    @Inject
    PublisherRepository publisherRepository;

    @Transactional
    public Publisher createPublisher(Publisher publisher) {
        publisherRepository.persist(publisher);
        return publisher;
    }

    public Publisher getPublisherById(Long id) {
        return publisherRepository.findById(id);
    }

    public List<Publisher> getAllPublishers() {
        return publisherRepository.listAll();
    }
}
