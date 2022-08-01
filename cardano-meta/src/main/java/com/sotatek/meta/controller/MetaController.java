package com.sotatek.meta.controller;

import com.sotatek.meta.document.MetaData;
import com.sotatek.meta.repository.MetaDataRepository;
import com.sotatek.meta.services.MetaDataService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/metadata")
public class MetaController {


    @Autowired
    MetaDataService metaDataService;

    private static final Logger LOGGER = Logger.getLogger(MetaController.class);

    @GetMapping(value = "/{subject}")
    public ResponseEntity<?> searchMetaDataBySubject(@PathVariable String subject) {
        try {
            MetaData metaData = metaDataService.findMetaDataBySubject(subject);
            if(metaData != null) {
                return ResponseEntity.ok(metaData);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested subject '" + subject + "' not found");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Somethings wrong !");
        }
    }
    @GetMapping(value = "/{subject}/properties/{property}")
    public ResponseEntity<?> searchMetaDataBySubjectAndProperty(@PathVariable String subject, @PathVariable String property) {
        try {
            MetaData metaData = metaDataService.findBySubjectAndGetProperty(subject, property);
            if(metaData != null) {
                return ResponseEntity.ok(metaData);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested subject '" + subject + "' not found");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Somethings wrong !");
        }
    }
}
