package com.CRUD.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.CRUD.Entity.UserEntity;
import com.CRUD.Exception.UserNotFound_Exception;
import com.CRUD.Implements.ServiceIMP;
import com.CRUD.Response.LoginResponse;
import com.CRUD.Response.LoginResponseHandler;
import com.CRUD.Response.ResponseHandler;
import com.CRUD.Util.AES256Util;
import com.CRUD.Util.EncryptionUtil;
import com.CRUD.Util.ExcelUtil;

import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/user")
public class Controller_Class {

	private final ServiceIMP serviceIMP;

	public Controller_Class(ServiceIMP serviceIMP) {
		this.serviceIMP = serviceIMP;
	}

	// Get All Users with pagination format 
	@GetMapping
	public ResponseEntity<Object> getAllUsers(@RequestParam(defaultValue = "0", required = false)Integer pageNumber,
			@RequestParam(defaultValue = "10",required = false)Integer pageSize){
		Page<UserEntity> entriesPage = (Page<UserEntity>) serviceIMP.getalluser(pageNumber,pageSize);
		return ResponseHandler.responseBuilder(entriesPage, HttpStatus.OK, "Records Found");
	}

	// Get Single User
	@GetMapping("{id}")
	public ResponseEntity<Object> getByID(@PathVariable("id") Integer id) {
		UserEntity userEntity = serviceIMP.getOneUser(id);
		return ResponseHandler.responseBuilder(userEntity, HttpStatus.OK, "Record Fetched Successfully");
	}

	@GetMapping("{id}/{name}")
	public ResponseEntity<Object> getByIdAndName(@PathVariable("id") Integer id, @PathVariable("name") String name) {
		List<UserEntity> users = serviceIMP.findByIdAndName(id, name);
		return ResponseHandler.responseBuilder(users, HttpStatus.OK, "Records Fetched Successfully");
	}
	
	@GetMapping("/users-between-dates/{fromDate}/{toDate}")
    public ResponseEntity<Object> getUsersBetweenDates(
            @PathVariable("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @PathVariable("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            List<UserEntity> users = serviceIMP.getUsersByRegistrationDateRange(fromDate, toDate);
            return ResponseHandler.responseBuilder(users, HttpStatus.OK, "Records Fetched Successfully");
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
            return ResponseHandler.responseBuilder(null, HttpStatus.INTERNAL_SERVER_ERROR, "Error Fetching Records");
        }
    }
	
	@GetMapping("/users-between-dates-paginated")
	public ResponseEntity<Object> getUsersBetweenDatesPaginated(
	        @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
	        @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
	        @RequestParam(defaultValue = "0") Integer pageNumber,
	        @RequestParam(defaultValue = "10") Integer pageSize) {
	    Map<String, Object> response = serviceIMP.getUsersByRegistrationDateRangeWithPagination(fromDate, toDate, pageNumber, pageSize);
	    return ResponseHandler.responseBuilder(response, HttpStatus.OK, "Records Found");
	}


	// Add New User
	@PostMapping
	public ResponseEntity<Object> addUser(@RequestBody UserEntity userEntity) {
		serviceIMP.addUser(userEntity);
		return ResponseHandler.responseBuilder(userEntity, HttpStatus.CREATED, "Record Created Successfully");
	}
	
	
	@PostMapping("/add-multiple-users")
	public ResponseEntity<Object> addMultipleUsers(@RequestBody List<UserEntity> users) {
		String response = serviceIMP.addMultipleUsers(users);
		return ResponseHandler.responseBuilder(response, HttpStatus.OK, "Users added successfully");
	}

	// Update User
	@PutMapping
	public ResponseEntity<Object> updateUser(@RequestBody UserEntity userEntity) {
		serviceIMP.updateUser(userEntity);
		return ResponseHandler.responseBuilder(userEntity, HttpStatus.OK, "Record Updated Successfully");
	}

	// Delete User
	@DeleteMapping("{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable("id") Integer id) {
		serviceIMP.deleteUser(id);
		return ResponseHandler.responseBuilder(id, HttpStatus.OK, "Record Deleted Successfully");
	}

	//code for Download csv file
	 @GetMapping("/download")
	    public ResponseEntity<byte[]> downloadUsersExcel(
	            @RequestParam(defaultValue = "0") int pageNumber,
	            @RequestParam(defaultValue = "100") int pageSize) {
	        try {
	            ByteArrayInputStream in = serviceIMP.streamExcel(pageNumber, pageSize);
	            byte[] bytes = in.readAllBytes();

	            HttpHeaders headers = new HttpHeaders();
	            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
	            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

	            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	        } catch (IOException e) {
	            e.printStackTrace();
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
    
    //login method 
	@PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserEntity loginRequest) {
        try {
            LoginResponse loginResponse = serviceIMP.login(loginRequest.getPhone(), loginRequest.getPassword());
            return LoginResponseHandler.responseBuilder(loginResponse);
        } catch (RuntimeException e) {
            return ResponseHandler.responseBuilder(null, HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseHandler.responseBuilder(null, HttpStatus.INTERNAL_SERVER_ERROR, "Encryption Error");
        }
    }
	
	
	//decryption mendpoint
	
	@PostMapping("/decrypt-token")
	public ResponseEntity<Object> decryptToken(@RequestBody Map<String, String> request) {
		try {
			String token = request.get("token");

			// Decrypt the token to get the JSON string
			String decryptedJson = AES256Util.decrypt(token);

			// Convert the JSON s tring back to a UserData object
			LoginResponse.UserData userData = AES256Util.jsonToObject(decryptedJson, LoginResponse.UserData.class);

			LoginResponse loginResponse = new LoginResponse("SUCCESS", 200, "Token Decryption Successful", userData,"Null" // If you want to include the token in the response, otherwise, set to an empty
							// string
			);
 
			return ResponseHandler.responseBuilder(userData, HttpStatus.OK, "Records Fetched Successfully");

		} catch (Exception e) {
			e.printStackTrace(); // Log the error for debugging
			return ResponseHandler.responseBuilder(null, HttpStatus.INTERNAL_SERVER_ERROR, "Token Decryption Error");
		}
	}

}