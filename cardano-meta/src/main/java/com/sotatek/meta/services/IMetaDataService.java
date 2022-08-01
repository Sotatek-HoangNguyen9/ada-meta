package com.sotatek.meta.services;

import com.sotatek.meta.document.MetaData;

public interface IMetaDataService {
    MetaData findMetaDataBySubject(String subject);
    MetaData findBySubjectAndGetProperty(String subject, String property);
}
