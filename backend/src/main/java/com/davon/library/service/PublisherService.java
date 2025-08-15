package com.davon.library.service;

import com.davon.library.model.Publisher;
import com.davon.library.repository.PublisherRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class PublisherService {

    @Inject
    PublisherRepository publisherRepository;

    public List<Publisher> getAllPublishers() {
        return publisherRepository.listAll();
    }
}
