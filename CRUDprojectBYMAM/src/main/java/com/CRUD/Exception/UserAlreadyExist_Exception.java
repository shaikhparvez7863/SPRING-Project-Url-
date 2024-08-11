package com.CRUD.Exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExist_Exception extends Costum_Exception {
	public UserAlreadyExist_Exception(String message) {
		super(Exception_Message.USER_ALREADY_EXISTS, HttpStatus.CONFLICT);
	}
}
