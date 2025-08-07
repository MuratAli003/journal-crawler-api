package com.DergiPlatform.DergiPlatform.repository;

import com.DergiPlatform.DergiPlatform.models.Sayi;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SayiRepository extends MongoRepository<Sayi, String> {
}
