package com.CRUD.Response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public class LoginResponseHandler {

    public static ResponseEntity<Object> responseBuilder(LoginResponse loginResponse) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", loginResponse.getStatus());
        response.put("statuscode", loginResponse.getStatuscode());
        response.put("message", loginResponse.getMessage());
        response.put("data", Map.of(
            "id", loginResponse.getData().getId(),
            "name", loginResponse.getData().getName(),
            "phone", loginResponse.getData().getPhone()
        ));
        response.put("token", loginResponse.getToken());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
