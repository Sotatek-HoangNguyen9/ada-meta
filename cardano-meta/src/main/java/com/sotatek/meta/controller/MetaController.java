package com.sotatek.meta.controller;

import com.sotatek.meta.response.ResponseFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/api/v1/meta")
public class MetaController {



    @Autowired
    private ResponseFactory responseFactory;

    private static final Logger LOGGER = Logger.getLogger(MetaController.class);

    @GetMapping(value = "/customer-profile/search-by-msisdn")
    public ResponseEntity<?> searchCampaignCustProfile(@RequestParam String msisdn) {
        try {
                   return responseFactory.success("OK");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return responseFactory.error(ex.getMessage());
        }
    }

    @GetMapping("/job")
    public ResponseEntity<?> getById(){
//        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
//        executor.scheduleAtFixedRate(() -> sendJob(), 0, 60, TimeUnit.SECONDS);
//        executor.scheduleAtFixedRate(() -> updateResponse(), 0, 60, TimeUnit.SECONDS);

        return ResponseEntity.ok().body("");
    }

//    @PostMapping(value = "/upsert/customer-event-store")
//    public ResponseEntity<?> upsertCustomerEventStore(@RequestBody List<CampaignCustEventStore> campaignCustEventStores) {
//        Integer response;
//        try {
//            if (campaignCustEventStores != null) {
//                response = sercetService.upsertCampaigCustEventStore(campaignCustEventStores);
//            } else {
//                response = 0;
//            }
//
//        } catch (Exception ex) {
//            response = 0;
//            LOGGER.error(ex.getMessage(), ex);
//        }
//        return responseFactory.success(response);
//    }

}
