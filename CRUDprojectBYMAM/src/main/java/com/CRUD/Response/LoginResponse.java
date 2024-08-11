package com.CRUD.Response;

public class LoginResponse {
    private String status;
    private int statuscode;
    private String message;
    private UserData data;
    private String token;

    // Constructors, getters, and setters
    

    public LoginResponse(String status, int statuscode, String message, UserData data, String token) {
        this.status = status;
        this.statuscode = statuscode;
        this.message = message;
        this.data = data;
        this.token = token;
    }

    public LoginResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	// Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(int statuscode) {
        this.statuscode = statuscode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // Inner class for user data
    public static class UserData {
        private Integer id;
        private String name;
        private String phone;

        // Constructors, getters, and setters
        
        public UserData(Integer id, String name, String phone) {
            this.id = id;
            this.name = name;
            this.phone = phone;
        }

        public UserData() {
			super();
			// TODO Auto-generated constructor stub
		}

		// Getters and Setters
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}

