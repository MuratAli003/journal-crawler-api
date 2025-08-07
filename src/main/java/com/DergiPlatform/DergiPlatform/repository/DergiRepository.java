package com.DergiPlatform.DergiPlatform.repository;

import com.DergiPlatform.DergiPlatform.models.Dergi;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DergiRepository extends MongoRepository<Dergi, String> {
    List<Dergi> findByNameContaining(String name);
}
