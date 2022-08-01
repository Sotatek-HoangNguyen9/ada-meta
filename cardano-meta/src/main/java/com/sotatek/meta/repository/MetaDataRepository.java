package com.sotatek.meta.repository;

import com.sotatek.meta.document.MetaData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface MetaDataRepository extends MongoRepository<MetaData, String> {
    MetaData findMetaDataBySubject(String subject);
}
