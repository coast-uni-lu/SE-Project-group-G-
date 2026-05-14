package com.group_g.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.group_g.demo.model.SeedMetadata;

public interface SeedMetadataRepository extends MongoRepository<SeedMetadata, String> {
    // spring reads the method's namee and automatically builds the querry based on it
    // storess which version of the question bank was loaded.
    // ReadJSON use the findById() func to check this to avoid uploading the same items twice in the db
}
