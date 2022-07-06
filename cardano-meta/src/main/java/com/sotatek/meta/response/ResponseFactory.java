package com.sotatek.meta.response;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


import java.util.Date;

@Component
public class ResponseFactory {
    public ResponseEntity<?> success(Object data) {
        GeneralResponse<Object> responseObject = new GeneralResponse<>();
        ResponseStatus responseStatus = new ResponseStatus();
        responseStatus.setCode("00");
        responseStatus.setMessage("Success");
        responseStatus.setResponseTime(new Date());
        responseStatus.setDisplayMessage("Thành công");
        responseObject.setStatus(responseStatus);
        responseObject.setData(data);

        return ResponseEntity.ok(responseObject);
    }
    public ResponseEntity<?> error(String message) {
        GeneralResponse<Object> responseObject = new GeneralResponse<>();
        ResponseStatus responseStatus = new ResponseStatus();
        responseStatus.setCode("01");
        responseStatus.setMessage(message);
        responseStatus.setResponseTime(new Date());
        responseStatus.setDisplayMessage("Thất bại");
        responseObject.setStatus(responseStatus);
        return ResponseEntity.ok(responseObject);
    }
}
