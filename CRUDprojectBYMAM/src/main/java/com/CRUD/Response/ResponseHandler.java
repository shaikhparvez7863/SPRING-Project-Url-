package com.CRUD.Response;
//This linkedList is must for maintain flow of message 
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    public static ResponseEntity<Object> responseBuilder(Object responseObject, HttpStatus httpStatus, String message) {
        logger.debug("Building response with status: {}, message: {}, code: {}", httpStatus.getReasonPhrase(), httpStatus.value(), message);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", httpStatus.getReasonPhrase());
        response.put("statuscode", httpStatus.value());
        response.put("message", message);
        response.put("data", responseObject);
        
        return new ResponseEntity<>(response, httpStatus);
    }
}