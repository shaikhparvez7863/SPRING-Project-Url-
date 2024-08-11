package com.CRUD.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHander {

	@ExceptionHandler(Costum_Exception.class)
	public ResponseEntity<Map<String, Object>> handleCustomException(Costum_Exception ex) {
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("Status", ex.getHttpStatus().getReasonPhrase());
		response.put("StatusCode", ex.getHttpStatus().value());
		response.put("message", ex.getMessage());
		response.put("data", null); // Data can be null for exceptions
		return new ResponseEntity<>(response, ex.getHttpStatus());
	}

	// You can add more exception handlers if needed
}