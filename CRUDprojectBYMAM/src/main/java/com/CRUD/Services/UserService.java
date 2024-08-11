package com.CRUD.Services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.CRUD.Entity.UserEntity;
import com.CRUD.Response.LoginResponse;


public interface UserService {

	public Page<UserEntity> getalluser(Integer pageNumber,Integer pageSize);

	public UserEntity getOneUser(Integer id);

	public String addUser(UserEntity userEntity);

	public String updateUser(UserEntity userEntity);

	public String deleteUser(Integer id);

	public List<UserEntity> findByIdAndName(Integer id, String name);

	// Add login method
	LoginResponse login(String phone, String password);

	UserEntity getOneUser(String phone);

	// Method to add multiple users
	String addMultipleUsers(List<UserEntity> users);

	// Fetch users by range of date
	public List<UserEntity> getUsersByRegistrationDateRange(LocalDate fromDate, LocalDate toDate);

	 // New method to fetch users by registration date range with pagination
    Map<String, Object> getUsersByRegistrationDateRangeWithPagination(LocalDate fromDate, LocalDate toDate, Integer pageNumber, Integer pageSize);

    
    //csv
    ByteArrayInputStream streamExcel(int pageNumber, int pageSize) throws IOException;
 }