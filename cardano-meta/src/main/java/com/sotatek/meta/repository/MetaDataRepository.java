package com.sotatek.meta.repository;

import com.sotatek.meta.document.MetaData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MetaDataRepository extends MongoRepository<MetaData, String> {
}
