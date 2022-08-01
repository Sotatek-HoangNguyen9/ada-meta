package com.sotatek.meta.services;


import com.sotatek.meta.document.MetaData;
import com.sotatek.meta.repository.MetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class MetaDataService implements IMetaDataService{

    @Autowired
    MetaDataRepository metaDataRepository;
    @Autowired
    MongoTemplate mongoTemplate;


    @Override
    public MetaData findMetaDataBySubject(String subject) {
        return metaDataRepository.findMetaDataBySubject(subject);
    }

    @Override
    public MetaData findBySubjectAndGetProperty(String subject, String property) {
        Query query = new Query(Criteria.where("subject").is(subject));
        query.fields().include(property);
        return mongoTemplate.findOne(query, MetaData.class);
    }
}
