package com.esewa.csmreactivedemo.repo;

import com.esewa.csmreactivedemo.model.FileContent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FileContentRepo extends ReactiveMongoRepository<FileContent, String> {
}
