package com.CRUD.Implements;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.CRUD.Entity.UserEntity;
import com.CRUD.Exception.BadRequest_Exception;
import com.CRUD.Exception.Exception_Message;
import com.CRUD.Exception.UserNotFound_Exception;
import com.CRUD.Repository.UserRepository;
import com.CRUD.Response.LoginResponse;
import com.CRUD.Response.ResponseHandler;
import com.CRUD.Services.UserService;
import com.CRUD.Util.AES256Util;
import com.CRUD.Util.EncryptionUtil;

@Service
public class ServiceIMP implements UserService {

	private final UserRepository userRepository;

	public ServiceIMP(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// Pagination---------
	@Override
	public Page<UserEntity> getalluser(Integer pageNumber, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<UserEntity> page = userRepository.findAll(pageable);

		return userRepository.findAll(pageable);
	}

	// fetch users by date range
	@Override
	public List<UserEntity> getUsersByRegistrationDateRange(LocalDate fromDate, LocalDate toDate) {
		// TODO Auto-generated method stub
		return userRepository.findByRegistrationDateBetween(fromDate, toDate);
	}

	
	// get user between the date i pagination format
	@Override
	public Map<String, Object> getUsersByRegistrationDateRangeWithPagination(LocalDate fromDate, LocalDate toDate,
			Integer pageNumber, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<UserEntity> page = userRepository.findByRegistrationDateBetween(fromDate, toDate, pageable);

		// Build the response map
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("Status", "OK");
		response.put("StatusCode", 200);
		response.put("message", "Records Found");
		response.put("totalRecords", page.getTotalElements());
		response.put("pageSize", page.getSize());
		response.put("currentPage", page.getNumber() + 1); // Page numbers are zero-based in Pageable
		response.put("totalPages", page.getTotalPages());
		response.put("data", page.getContent());

		return response;

	}

	//csv file generation 
	 @Override
	    public ByteArrayInputStream streamExcel(int pageNumber, int pageSize) throws IOException {
	        Pageable pageable = PageRequest.of(pageNumber, pageSize);
	        Page<UserEntity> page = userRepository.findAll(pageable); // Fetch paginated data

	        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	            Sheet sheet = workbook.createSheet("Users");

	            // Create header row
	            Row header = sheet.createRow(0);
	            header.createCell(0).setCellValue("ID");
	            header.createCell(1).setCellValue("Name");
	            header.createCell(2).setCellValue("Phone");
	            header.createCell(3).setCellValue("Password");
	            header.createCell(4).setCellValue("Registration Date");

	            // Create data rows
	            int rowNum = 1;
	            for (UserEntity user : page.getContent()) {
	                Row row = sheet.createRow(rowNum++);

	                row.createCell(0).setCellValue(user.getId());
	                row.createCell(1).setCellValue(user.getName());
	                row.createCell(2).setCellValue(user.getPhone());
	                row.createCell(3).setCellValue(user.getPassword());
	                row.createCell(4).setCellValue(user.getRegistrationDate().toString());
	            }

	            workbook.write(out);
	            return new ByteArrayInputStream(out.toByteArray());
	        }
	    }
	 
	 
	// get single user data
	@Override
	public UserEntity getOneUser(Integer id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new UserNotFound_Exception(Exception_Message.USER_NOT_FOUND));
	}

	// add single user
	@Override
	public String addUser(UserEntity userEntity) {
		validateEntry(userEntity);
		if (userRepository.findByPhone(userEntity.getPhone()).isPresent()) {
			throw new BadRequest_Exception(Exception_Message.Phone_Conflict);
		}
		userEntity.setPassword(EncryptionUtil.sha256(userEntity.getPassword()));
		userRepository.save(userEntity);
		return "User Added Successfully";
	}

	// Multiple user insert
	@Override
	public String addMultipleUsers(List<UserEntity> users) {
		List<String> existingPhones = users.stream().map(UserEntity::getPhone)
				.filter(phone -> userRepository.findByPhone(phone).isPresent()).collect(Collectors.toList());

		if (!existingPhones.isEmpty()) {
			throw new BadRequest_Exception("Phones already in use: " + existingPhones);
		}

		users.forEach(this::validateEntry);
		users.forEach(user -> user.setPassword(EncryptionUtil.sha256(user.getPassword())));
		userRepository.saveAll(users);
		return "Users Added Successfully";
	}

	// update user
	@Override
	public String updateUser(UserEntity userEntity) {
		validateEntry(userEntity);
		if (!userRepository.existsById(userEntity.getId())) {
			throw new UserNotFound_Exception(Exception_Message.USER_NOT_FOUND);
		}
		Optional<UserEntity> existingUser = userRepository.findByPhone(userEntity.getPhone());
		if (existingUser.isPresent() && !existingUser.get().getId().equals(userEntity.getId())) {
			throw new BadRequest_Exception("Phone number already in use by another user");
		}
		userEntity.setPassword(EncryptionUtil.sha256(userEntity.getPassword()));
		userRepository.save(userEntity);
		return "User Updated Successfully";
	}

	// delete user
	@Override
	public String deleteUser(Integer id) {
		if (!userRepository.existsById(id)) {
			throw new UserNotFound_Exception(Exception_Message.USER_NOT_FOUND);
		}
		userRepository.deleteById(id);
		return "User Deleted Successfully";
	}

	// fetch user by id and name
	@Override
	public List<UserEntity> findByIdAndName(Integer id, String name) {
		return userRepository.findByIdAndName(id, name);
	}

	// Login method
	@Override
	public LoginResponse login(String phone, String password) {
		return userRepository.findByPhone(phone)
				.filter(user -> EncryptionUtil.sha256(password).equals(user.getPassword())).map(user -> {
					String tokenData = String.format("{\"id\":\"%d\",\"name\":\"%s\",\"phone\":\"%s\"}", user.getId(),
							user.getName(), user.getPhone());
					String token;
					try {
						token = AES256Util.encrypt(tokenData).toUpperCase();
//						token.toUpperCase();
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("Error generating token: " + e.getMessage());
					}
					LoginResponse.UserData userData = new LoginResponse.UserData(user.getId(), user.getName(),
							user.getPhone());
					return new LoginResponse("SUCCESS", 200, "Login Successfully", userData, token);
				}).orElseThrow(() -> new RuntimeException("Invalid phone number or password"));
	}

	// Logic for Validation
	private void validateEntry(UserEntity entry) {
		if (entry.getName() == null || entry.getName().trim().isEmpty()) {
			throw new BadRequest_Exception(Exception_Message.ID_INVALID_METHODE);
		}
		if (entry.getPhone() == null || entry.getPhone().trim().isEmpty()) {
			throw new BadRequest_Exception(Exception_Message.PHONE_INVALID_METHODE);
		}
		if (entry.getPhone().length() != 10) {
			throw new BadRequest_Exception(Exception_Message.Phone_Size);
		}
		if (entry.getPassword() == null || entry.getPassword().trim().isEmpty()) {
			throw new BadRequest_Exception(Exception_Message.PASSWORD_INVALID_METHODE);
		}
	}

	@Override
	public UserEntity getOneUser(String phone) {
		return userRepository.findByPhone(phone)
				.orElseThrow(() -> new UserNotFound_Exception(Exception_Message.USER_NOT_FOUND));
	}

}