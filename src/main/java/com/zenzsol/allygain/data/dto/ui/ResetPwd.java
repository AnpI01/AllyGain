package com.zenzsol.allygain.data.dto.ui;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ResetPwd {
	private String token;
	@NotEmpty(message = "Password can't be empty.")
    @Size(min = 8, max = 25, message = "Min 8 characters.")
	private String password;
	@NotEmpty(message = "Re enter password")
    @Size(min = 8, max = 25, message = "Min 8 characters.")
	private String repassword;
	boolean submitted;
	
	public boolean isSubmitted() {
		return submitted;
	}
	public void setSubmitted(boolean submitted) {
		this.submitted = submitted;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRepassword() {
		return repassword;
	}
	public void setRepassword(String repassword) {
		this.repassword = repassword;
	}
	
	
}
