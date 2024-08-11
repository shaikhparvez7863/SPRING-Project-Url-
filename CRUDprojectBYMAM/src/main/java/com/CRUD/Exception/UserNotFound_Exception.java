package com.CRUD.Exception;

import org.springframework.http.HttpStatus;

public class UserNotFound_Exception extends Costum_Exception {
	public UserNotFound_Exception(String message) {
		super(Exception_Message.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
	}
}